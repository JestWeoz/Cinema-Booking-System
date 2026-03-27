package org.example.cinemaBooking.Controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Request.Ticket.CheckInRequest;
import org.example.cinemaBooking.Dto.Response.Ticket.CheckInResponse;
import org.example.cinemaBooking.Dto.Response.Ticket.TicketResponse;
import org.example.cinemaBooking.Service.Ticket.TicketService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(ApiPaths.API_V1 + ApiPaths.Ticket.BASE)
public class TicketController {
    TicketService ticketService;

    /** Xem tất cả vé của mình */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<TicketResponse>> getMyTickets() {
        return ApiResponse.<List<TicketResponse>>builder()
                .success(true)
                .message("Tickets retrieved successfully")
                .data(ticketService.getMyTickets())
                .build();
    }

    /** Xem vé theo booking */
    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<TicketResponse>> getTicketsByBooking(
            @PathVariable String bookingId) {
        return ApiResponse.<List<TicketResponse>>builder()
                .success(true)
                .message("Tickets retrieved successfully")
                .data(ticketService.getTicketsByBooking(bookingId))
                .build();
    }

    /** Lấy QR code — base64 PNG */
    @GetMapping("/{bookingCode}/qr")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> getQRCode(
            @PathVariable String bookingCode) {
        return ApiResponse.<String>builder()
                .success(true)
                .message("QR code retrieved successfully")
                .data(ticketService.getBookingQR(bookingCode))
                .build();
    }

    /**
     * Check-in tại cửa rạp — nhân viên scan QR.
     * Chỉ STAFF/ADMIN mới được gọi.
     */
    @PostMapping("/check-in-ticket")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ApiResponse<CheckInResponse> checkIn(
            @Valid @RequestBody CheckInRequest request) {
        return ApiResponse.<CheckInResponse>builder()
                .success(true)
                .message("Check-in successful")
                .data(ticketService.checkIn(request))
                .build();
    }

    @PostMapping("/check-in")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ApiResponse<List<CheckInResponse>> checkInMultiple(
            @Valid @RequestBody CheckInRequest requests) {
        return ApiResponse.<List<CheckInResponse>>builder()
                .success(true)
                .message("Check-in successful")
                .data(ticketService.checkInByBookingCode(requests.bookingCode()))
                .build();
    }
}
