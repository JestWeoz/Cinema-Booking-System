package org.example.cinemaBooking.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.DTO.Request.Payment.CreatePaymentRequest;
import org.example.cinemaBooking.DTO.Response.Payment.PaymentResponse;
import org.example.cinemaBooking.Service.Payment.PaymentService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequestMapping(ApiPaths.API_V1 + ApiPaths.Payment.BASE)
@Tag(name = "Payment", description = "xử lý thanh toán, IPN và trả về từ VNPay")
public class PaymentController {
    PaymentService paymentService;

    @Operation(summary = "Tạo thanh toán mới",
            description = "Tạo một thanh toán mới cho một đặt vé. Yêu cầu người dùng đã xác thực.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            HttpServletRequest httpRequest
    ) {
        PaymentResponse paymentResponse = paymentService.createPayment(request, httpRequest);
        return ApiResponse.<PaymentResponse>builder().
                success(true).
                message("Payment created successfully").
                data(paymentResponse).
                build();
    }

    @Operation(summary = "Xử lý trả về từ VNPay",
            description = "Xử lý dữ liệu trả về sau khi người dùng thanh toán trên VNPay. Thường được gọi bởi redirect của VNPay.")
    @GetMapping("/vnpay/return")
    public ApiResponse<PaymentResponse> handleReturn(
            @RequestParam Map<String, String> params) {
        return ApiResponse.<PaymentResponse>builder().
                success(true).
                message("Payment processed successfully").
                data(paymentService.handleReturn(params)).
                build();
    }

    @Operation(summary = "IPN từ VNPay",
            description = "Endpoint nhận Instant Payment Notification (IPN) từ VNPay để cập nhật trạng thái thanh toán.")
    @GetMapping("/vnpay/ipn")
    public ApiResponse<String> handleIPN(
            @RequestParam Map<String, String> params) {
        String result = paymentService.handleIPN(params);
        log.info("IPN processed with result: {}", result);
        return ApiResponse.<String>builder().
                success(true).
                message("IPN processed successfully").
                data(result).
                build();
    }

    /**
     * Xem payment theo bookingId
     */
    @Operation(summary = "Lấy thông tin thanh toán theo booking",
            description = "Lấy chi tiết thanh toán liên quan đến bookingId.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PaymentResponse> getPaymentByBooking(
            @PathVariable String bookingId) {
        return ApiResponse.<PaymentResponse>builder().
                success(true).
                message("Payment retrieved successfully").
                data(paymentService.getPaymentByBookingId(bookingId)).
                build();
    }

    /**
     * Hoàn tiền — admin only
     */
    @Operation(summary = "Hoàn tiền cho booking",
            description = "Thực hiện hoàn tiền cho một booking cụ thể (chỉ ADMIN).",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/booking/{bookingId}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PaymentResponse> refund(
            @PathVariable String bookingId) {
        return ApiResponse.<PaymentResponse>builder().
                success(true).
                message("Refund processed successfully").
                data(paymentService.refund(bookingId)).
                build();
    }


}
