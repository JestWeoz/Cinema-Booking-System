package org.example.cinemaBooking.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@RequestMapping(ApiPaths.API_V1 + ApiPaths.Payment.BASE)
public class PaymentController {
    PaymentService paymentService;

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

    @GetMapping("/vnpay/return")
    public ApiResponse<PaymentResponse> handleReturn(
            @RequestParam Map<String, String> params) {
        return ApiResponse.<PaymentResponse>builder().
                success(true).
                message("Payment processed successfully").
                data(paymentService.handleReturn(params)).
                build();
    }

    @PostMapping("/vnpay/ipn")
    public ApiResponse<String> handleIPN(
            @RequestParam Map<String, String> params) {
        String result = paymentService.handleIPN(params);
        return ApiResponse.<String>builder().
                success(true).
                message("IPN processed successfully").
                data(result).
                build();
    }

    /**
     * Xem payment theo bookingId
     */
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
