package org.example.cinemaBooking.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Request.Booking.CreateBookingRequest;
import org.example.cinemaBooking.Dto.Response.Booking.BookingResponse;
import org.example.cinemaBooking.Dto.Response.Booking.BookingSummaryResponse;
import org.example.cinemaBooking.Service.Booking.BookingService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(ApiPaths.API_V1 + ApiPaths.Booking.BASE)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {
    BookingService bookingService;
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        var response = bookingService.createBooking(request);
        log.info("[BookingController] createBooking - Booking created with code: {}", response.bookingCode());
        return ApiResponse.<BookingResponse>builder()
                .data(response)
                .success(true)
                .message("Booking created successfully")
                .build();
    }

    @GetMapping("/{bookingId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<BookingResponse> getBookingById(@PathVariable String bookingId) {
        var response = bookingService.getBookingById(bookingId);
        log.info("[BookingController] getBookingById - Retrieved booking with code: {}", response.bookingCode());
        return ApiResponse.<BookingResponse>builder()
                .data(response)
                .success(true)
                .message("Booking retrieved successfully")
                .build();
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<BookingSummaryResponse>> getMyBookings() {
        var response = bookingService.getMyBookings();
        log.info("[BookingController] getMyBookings - Retrieved {} bookings for current user", response.size());
        return ApiResponse.<List<BookingSummaryResponse>>builder()
                .data(response)
                .success(true)
                .message("My bookings retrieved successfully")
                .build();
    }

    @PatchMapping("/{bookingId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> cancelBooking(@PathVariable String bookingId) {
        bookingService.cancelBooking(bookingId);
        log.info("[BookingController] cancelBooking - Canceled booking with ID: {}", bookingId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Booking canceled successfully")
                .build();
    }

}
