package org.example.cinemaBooking.Service.Payment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Config.VNPayConfig;
import org.example.cinemaBooking.DTO.Request.Payment.CreatePaymentRequest;
import org.example.cinemaBooking.DTO.Response.Payment.PaymentResponse;
import org.example.cinemaBooking.Entity.Booking;
import org.example.cinemaBooking.Entity.Payment;
import org.example.cinemaBooking.Exception.AppException;
import org.example.cinemaBooking.Exception.ErrorCode;
import org.example.cinemaBooking.Mapper.PaymentMapper;
import org.example.cinemaBooking.Repository.BookingRepository;
import org.example.cinemaBooking.Repository.PaymentRepository;
import org.example.cinemaBooking.Service.Auth.EmailService;
import org.example.cinemaBooking.Service.Booking.BookingService;
import org.example.cinemaBooking.Service.Notification.NotificationService;
import org.example.cinemaBooking.Service.Showtime.ShowTimeSeatService;
import org.example.cinemaBooking.Shared.enums.BookingStatus;
import org.example.cinemaBooking.Shared.enums.PaymentMethod;
import org.example.cinemaBooking.Shared.enums.PaymentStatus;
import org.example.cinemaBooking.Shared.enums.VNPayUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class PaymentService {

    PaymentRepository   paymentRepository;
    BookingRepository   bookingRepository;
    BookingService      bookingService;
    ShowTimeSeatService showtimeSeatService;
    PaymentMapper       paymentMapper;
    VNPayConfig         vnPayConfig;
    NotificationService notificationService;
    EmailService        emailService;

    // ─────────────────────────────────────────────────────────────────
    // TẠO PAYMENT + REDIRECT URL
    // ─────────────────────────────────────────────────────────────────

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request,
                                         HttpServletRequest httpRequest) {
        Booking booking = bookingRepository.findByIdWithDetails(request.bookingId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new AppException(ErrorCode.BOOKING_STATUS_INVALID);
        }
        if (booking.isExpired()) {
            throw new AppException(ErrorCode.BOOKING_EXPIRED);
        }

        String clientIp = resolveClientIp(request, httpRequest);

        // FIX P5: tái sử dụng payment PENDING — check cả isExpired() của booking
        var existing = paymentRepository.findByBookingId(booking.getId());
        if (existing.isPresent() && existing.get().getStatus() == PaymentStatus.PENDING) {
            Payment existingPayment = existing.get();
            String url = buildVNPayUrl(booking, existingPayment, clientIp);
            PaymentResponse resp = paymentMapper.toResponse(existingPayment);
            return withPaymentUrl(resp, url);
        }

        Payment payment = Payment.builder()
                .booking(booking)
                .paymentMethod(PaymentMethod.VNPAY)
                .status(PaymentStatus.PENDING)
                .amount(booking.getFinalPrice())
                .build();

        Payment saved = paymentRepository.save(payment);
        String paymentUrl = buildVNPayUrl(booking, saved, clientIp);

        log.info("Payment created: bookingCode={}, amount={}, ip={}",
                booking.getBookingCode(), booking.getFinalPrice(), clientIp);

        return withPaymentUrl(paymentMapper.toResponse(saved), paymentUrl);
    }

    // ─────────────────────────────────────────────────────────────────
    // IPN — VNPay gọi server-to-server
    // ─────────────────────────────────────────────────────────────────

    /**
     * FIX P1: handleIPN KHÔNG có @Transactional bao ngoài.
     *
     * Thay vào đó:
     *   - savePaymentStatus()   chạy trong REQUIRES_NEW transaction riêng → commit ngay
     *   - confirmBooking()      chạy trong REQUIRES_NEW transaction riêng
     *   - Nếu confirmBooking() fail sau khi payment đã commit → payment vẫn được lưu,
     *     cần alerting/manual review (không bị mất dữ liệu).
     *
     * FIX P2: payment FAILED chỉ đánh dấu payment, KHÔNG cancel booking.
     *         User có thể thử lại payment; booking tự expire qua scheduled job.
     *
     * FIX P3: idempotent check TRƯỚC verify amount.
     *
     * FIX P4: xóa biến promotion dead code.
     */
    public String handleIPN(Map<String, String> params) {
        // 1. Verify chữ ký trước tiên
        if (!VNPayUtil.verifySecureHash(params, vnPayConfig.getHashSecret())) {
            log.warn("IPN invalid signature: {}", params);
            return "{\"RspCode\":\"97\",\"Message\":\"Invalid signature\"}";
        }

        String bookingCode  = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo = params.get("vnp_TransactionNo");
        String amountStr    = params.get("vnp_Amount");

        Payment payment = paymentRepository.findByBookingCode(bookingCode).orElse(null);
        if (payment == null) {
            log.warn("IPN: payment not found for bookingCode={}", bookingCode);
            return "{\"RspCode\":\"01\",\"Message\":\"Order not found\"}";
        }

        // FIX P3: idempotent check TRƯỚC verify amount
        if (payment.getStatus() != PaymentStatus.PENDING) {
            log.info("IPN: already processed bookingCode={}", bookingCode);
            return "{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}";
        }

        // Verify amount
        BigDecimal expectedAmount = payment.getAmount().multiply(BigDecimal.valueOf(100));
        BigDecimal receivedAmount = new BigDecimal(amountStr);
        if (expectedAmount.compareTo(receivedAmount) != 0) {
            log.warn("IPN: amount mismatch booking={} expected={} received={}",
                    bookingCode, expectedAmount, receivedAmount);
            return "{\"RspCode\":\"04\",\"Message\":\"Amount invalid\"}";
        }

        if ("00".equals(responseCode)) {
            // FIX P1: save payment trong transaction riêng — commit trước confirmBooking
            savePaymentStatus(payment.getId(), PaymentStatus.SUCCESS, transactionNo);

            try {
                // FIX P1: confirmBooking trong transaction riêng
                bookingService.confirmBooking(payment.getBooking().getId());
            } catch (Exception e) {
                // Payment đã commit SUCCESS — log alert để xử lý thủ công nếu cần
                log.error("IPN: confirmBooking failed after payment saved! " +
                        "bookingCode={}, error={}", bookingCode, e.getMessage(), e);
                // Vẫn trả "00" cho VNPay — tránh VNPay retry vô tận
                // Cần alerting system để ops team xử lý edge case này
                return "{\"RspCode\":\"00\",\"Message\":\"Confirm success\"}";
            }

            log.info("IPN: payment success bookingCode={} txn={}", bookingCode, transactionNo);

            // Gửi notification async — không block IPN response
            Booking booking = paymentRepository.findByBookingCode(bookingCode)
                    .map(Payment::getBooking).orElse(null);
            if (booking != null) {
                notificationService.notifyPaymentSuccess(booking);
                notificationService.notifyBookingSuccess(booking);
                emailService.sendBookingSuccessEmail(booking);
            }

        } else {
            // FIX P2: payment FAILED → chỉ đánh dấu payment, KHÔNG cancel booking.
            // Booking vẫn PENDING, user có thể thử lại payment trong thời gian expiredAt.
            // Scheduled job expireStaleBookings() sẽ tự dọn khi hết hạn.
            savePaymentStatus(payment.getId(), PaymentStatus.FAILED, transactionNo);

            log.warn("IPN: payment failed bookingCode={} responseCode={}",
                    bookingCode, responseCode);

            Booking booking = paymentRepository.findByBookingCode(bookingCode)
                    .map(Payment::getBooking).orElse(null);
            if (booking != null) {
                notificationService.notifyBookingCancelled(booking);
            }
        }

        return "{\"RspCode\":\"00\",\"Message\":\"Confirm success\"}";
    }

    // ─────────────────────────────────────────────────────────────────
    // RETURN URL — chỉ hiển thị kết quả, KHÔNG update DB
    // ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PaymentResponse handleReturn(Map<String, String> params) {
        if (!VNPayUtil.verifySecureHash(params, vnPayConfig.getHashSecret())) {
            throw new AppException(ErrorCode.PAYMENT_INVALID_SIGNATURE);
        }
        String bookingCode = params.get("vnp_TxnRef");
        Payment payment = paymentRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
        //tam thoi tra ve nhu nay, sau nay co the hien thi trang thanh toan thanh cong hay that bai dua tren payment status
        return paymentMapper.toResponse(payment);
    }

    // ─────────────────────────────────────────────────────────────────
    // REFUND
    // ─────────────────────────────────────────────────────────────────

    /**
     * FIX P6: refund phải cancel booking và release ghế.
     *
     * Flow:
     *   1. Đánh dấu payment REFUNDED
     *   2. Cancel booking → ghế BOOKED → AVAILABLE
     *   3. Gửi notification
     */
    @Transactional
    public PaymentResponse refund(String bookingId) {
        Payment payment = paymentRepository.findByBookingIdWithDetails(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new AppException(ErrorCode.PAYMENT_STATE_INVALID);
        }

        // FIX P6: đánh dấu payment REFUNDED
        payment.setStatus(PaymentStatus.REFUNDED);
        Payment saved = paymentRepository.save(payment);

        // FIX P6: cancel booking + release ghế BOOKED → AVAILABLE
        Booking booking = payment.getBooking();
        bookingService.cancelBookingInternal(booking.getId());

        log.info("Payment refunded: bookingId={}, amount={}", bookingId, payment.getAmount());

        emailService.sendCancelledEmail(booking);
        notificationService.notifyBookingCancelled(booking);

        return paymentMapper.toResponse(saved);
    }

    // ─────────────────────────────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBookingId(String bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
        return paymentMapper.toResponse(payment);
    }

    // ─────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────

    /**
     * FIX P1: save payment status trong transaction REQUIRES_NEW độc lập.
     * Commit ngay lập tức, không bị rollback bởi transaction cha (nếu có).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savePaymentStatus(String paymentId, PaymentStatus status, String transactionNo) {
        paymentRepository.findById(paymentId).ifPresent(p -> {
            p.setStatus(status);
            p.setTransactionId(transactionNo);
            paymentRepository.save(p);
        });
    }

    private String buildVNPayUrl(Booking booking, Payment payment, String clientIp) {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version",   vnPayConfig.getVersion());
        params.put("vnp_Command",   vnPayConfig.getCommand());
        params.put("vnp_TmnCode",   vnPayConfig.getTmnCode());
        params.put("vnp_Amount",    toVNPayAmount(booking.getFinalPrice()));
        params.put("vnp_CurrCode",  vnPayConfig.getCurrencyCode());
        params.put("vnp_TxnRef",    booking.getBookingCode());
        params.put("vnp_OrderInfo", "Thanh toan ve xem phim " + booking.getBookingCode());
        params.put("vnp_OrderType", "billpayment");
        params.put("vnp_Locale",    vnPayConfig.getLocale());
        params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        params.put("vnp_IpAddr",    clientIp);
        params.put("vnp_CreateDate", VNPayUtil.getCurrentTime());
        params.put("vnp_ExpireDate", VNPayUtil.getExpireTime(15));

        log.info("Client IP used for vnp_IpAddr: {}", clientIp);

        return VNPayUtil.buildPaymentUrl(
                vnPayConfig.getPaymentUrl(), params, vnPayConfig.getHashSecret());
    }

    private String toVNPayAmount(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).toBigInteger().toString();
    }

    private String resolveClientIp(CreatePaymentRequest request,
                                   HttpServletRequest httpRequest) {
        if (request.clientIp() != null && !request.clientIp().isBlank()) {
            return request.clientIp();
        }
        String xForwardedFor = httpRequest.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return httpRequest.getRemoteAddr();
    }

    private PaymentResponse withPaymentUrl(PaymentResponse resp, String url) {
        return new PaymentResponse(
                resp.paymentId(), resp.bookingId(), resp.bookingCode(),
                resp.paymentMethod(), resp.status(), resp.amount(),
                resp.transactionId(), url, resp.createdAt()
        );
    }
}