// PaymentService.java
package org.example.cinemaBooking.Service.Payment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Config.VNPayConfig;
import org.example.cinemaBooking.Dto.Request.Payment.CreatePaymentRequest;
import org.example.cinemaBooking.Dto.Response.Payment.PaymentResponse;
import org.example.cinemaBooking.Entity.Booking;
import org.example.cinemaBooking.Entity.Payment;
import org.example.cinemaBooking.Entity.Promotion;
import org.example.cinemaBooking.Exception.AppException;
import org.example.cinemaBooking.Exception.ErrorCode;
import org.example.cinemaBooking.Mapper.PaymentMapper;
import org.example.cinemaBooking.Repository.BookingRepository;
import org.example.cinemaBooking.Repository.PaymentRepository;
import org.example.cinemaBooking.Service.Auth.EmailService;
import org.example.cinemaBooking.Service.Booking.BookingService;
import org.example.cinemaBooking.Service.Notification.NotificationService;
import org.example.cinemaBooking.Service.Promotion.PromotionService;
import org.example.cinemaBooking.Shared.enums.BookingStatus;
import org.example.cinemaBooking.Shared.enums.PaymentMethod;
import org.example.cinemaBooking.Shared.enums.PaymentStatus;
import org.example.cinemaBooking.Shared.enums.VNPayUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class PaymentService {

    PaymentRepository paymentRepository;
    BookingRepository bookingRepository;
    BookingService bookingService;
    PaymentMapper paymentMapper;
    VNPayConfig vnPayConfig;
    NotificationService notificationService;
    EmailService emailService;
    PromotionService promotionService;
    // ─────────────────────────────────────────────────────────────────
    // TẠO PAYMENT + REDIRECT URL
    // ─────────────────────────────────────────────────────────────────

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request,
                                         HttpServletRequest httpRequest) {
        Booking booking = bookingRepository.findByIdWithDetails(request.bookingId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        // Chỉ tạo payment cho booking PENDING chưa hết hạn
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new AppException(ErrorCode.BOOKING_STATUS_INVALID);
        }
        if (booking.isExpired()) {
            throw new AppException(ErrorCode.BOOKING_EXPIRED);
        }

        // Nếu đã có payment PENDING → trả lại paymentUrl cũ (tránh tạo trùng)
        var existing = paymentRepository.findByBookingId(booking.getId());
        if (existing.isPresent() && existing.get().getStatus() == PaymentStatus.PENDING) {
            String url = buildVNPayUrl(booking, existing.get(),
                    resolveClientIp(request, httpRequest));
            PaymentResponse resp = paymentMapper.toResponse(existing.get());
            return new PaymentResponse(
                    resp.paymentId(), resp.bookingId(), resp.bookingCode(),
                    resp.paymentMethod(), resp.status(), resp.amount(),
                    resp.transactionId(), url, resp.createdAt()
            );
        }

        // Tạo Payment mới
        Payment payment = Payment.builder()
                .booking(booking)
                .paymentMethod(PaymentMethod.VNPAY)
                .status(PaymentStatus.PENDING)
                .amount(booking.getFinalPrice())
                .build();

        Payment saved = paymentRepository.save(payment);

        String clientIp = resolveClientIp(request, httpRequest);
        String paymentUrl = buildVNPayUrl(booking, saved, clientIp);

        log.info("Payment created: bookingCode={}, amount={}, ip={}",
                booking.getBookingCode(), booking.getFinalPrice(), clientIp);

        PaymentResponse resp = paymentMapper.toResponse(saved);
        return new PaymentResponse(
                resp.paymentId(), resp.bookingId(), resp.bookingCode(),
                resp.paymentMethod(), resp.status(), resp.amount(),
                resp.transactionId(), paymentUrl, resp.createdAt()
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // IPN — VNPay gọi server-to-server (quan trọng hơn return URL)
    // ─────────────────────────────────────────────────────────────────

    @Transactional
    public String handleIPN(Map<String, String> params) {
        // 1. Verify chữ ký
        if (!VNPayUtil.verifySecureHash(params, vnPayConfig.getHashSecret())) {
            log.warn("IPN invalid signature: {}", params);
            return "{\"RspCode\":\"97\",\"Message\":\"Invalid signature\"}";
        }

        String bookingCode = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo = params.get("vnp_TransactionNo");
        String amountStr = params.get("vnp_Amount"); // VNPay gửi amount * 100

        Payment payment = paymentRepository.findByBookingCode(bookingCode)
                .orElse(null);

        if (payment == null) {
            log.warn("IPN: payment not found for bookingCode={}", bookingCode);
            return "{\"RspCode\":\"01\",\"Message\":\"Order not found\"}";
        }

        // Idempotent — tránh xử lý 2 lần
        if (payment.getStatus() != PaymentStatus.PENDING) {
            log.info("IPN: already processed bookingCode={}", bookingCode);
            return "{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}";
        }

        // 2. Verify amount
        BigDecimal expectedAmount = payment.getAmount()
                .multiply(BigDecimal.valueOf(100));
        BigDecimal receivedAmount = new BigDecimal(amountStr);

        if (expectedAmount.compareTo(receivedAmount) != 0) {
            log.warn("IPN: amount mismatch booking={} expected={} received={}",
                    bookingCode, expectedAmount, receivedAmount);
            return "{\"RspCode\":\"04\",\"Message\":\"Amount invalid\"}";
        }

        // 3. Xử lý kết quả
        if ("00".equals(responseCode)) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(transactionNo);
            paymentRepository.save(payment);

            Booking booking = payment.getBooking();
            Promotion promotion = booking.getPromotion();


            // Confirm booking — ghế LOCKED → BOOKED
            bookingService.confirmBooking(payment.getBooking().getId());

            log.info("IPN: payment success bookingCode={} txn={}", bookingCode, transactionNo);

            // Gửi async — không block IPN response
            notificationService.notifyPaymentSuccess(payment.getBooking());
            notificationService.notifyBookingSuccess(payment.getBooking());
            emailService.sendBookingSuccessEmail(payment.getBooking());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setTransactionId(transactionNo);
            paymentRepository.save(payment);

            // Cancel booking — ghế LOCKED → AVAILABLE
            bookingService.cancelBooking(payment.getBooking().getId());

            log.warn("IPN: payment failed bookingCode={} responseCode={}", bookingCode, responseCode);

            notificationService.notifyBookingCancelled(payment.getBooking());
            emailService.sendCancelledEmail(payment.getBooking());
        }

        return "{\"RspCode\":\"00\",\"Message\":\"Confirm success\"}";
    }

    // ─────────────────────────────────────────────────────────────────
    // RETURN URL — user redirect về sau khi thanh toán
    // Chỉ dùng để hiển thị kết quả cho user, KHÔNG update DB ở đây
    // (IPN đã xử lý rồi)
    // ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PaymentResponse handleReturn(Map<String, String> params) {
        if (!VNPayUtil.verifySecureHash(params, vnPayConfig.getHashSecret())) {
            throw new AppException(ErrorCode.PAYMENT_INVALID_SIGNATURE);
        }

        String bookingCode = params.get("vnp_TxnRef");
        Payment payment = paymentRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        return paymentMapper.toResponse(payment);
    }

    // ─────────────────────────────────────────────────────────────────
    // REFUND — khi cancel booking đã confirmed
    // ─────────────────────────────────────────────────────────────────

    @Transactional
    public PaymentResponse refund(String bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new AppException(ErrorCode.PAYMENT_STATE_INVALID);
        }


        // Hiện tại chỉ đánh dấu REFUNDED — implement sau khi có merchant account thật
        payment.setStatus(PaymentStatus.REFUNDED);
        Payment saved = paymentRepository.save(payment);

        log.info("Payment refunded: bookingId={}, amount={}", bookingId, payment.getAmount());
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

    private String buildVNPayUrl(Booking booking, Payment payment, String clientIp) {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", vnPayConfig.getVersion());
        params.put("vnp_Command", vnPayConfig.getCommand());
        params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        params.put("vnp_Amount", toVNPayAmount(booking.getFinalPrice()));
        params.put("vnp_CurrCode", vnPayConfig.getCurrencyCode());
        params.put("vnp_TxnRef", booking.getBookingCode());   // unique per transaction
        params.put("vnp_OrderInfo", "Thanh toan ve xem phim " + booking.getBookingCode());
        params.put("vnp_OrderType", "billpayment");
        params.put("vnp_Locale", vnPayConfig.getLocale());
        params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        params.put("vnp_IpAddr", clientIp);
        params.put("vnp_CreateDate", VNPayUtil.getCurrentTime());
        params.put("vnp_ExpireDate", VNPayUtil.getExpireTime(15)); // hết hạn sau 15 phút

        return VNPayUtil.buildPaymentUrl(
                vnPayConfig.getPaymentUrl(), params, vnPayConfig.getHashSecret());
    }

    /**
     * VNPay yêu cầu amount * 100, không có dấu thập phân
     */
    private String toVNPayAmount(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100))
                .toBigInteger().toString();
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
}