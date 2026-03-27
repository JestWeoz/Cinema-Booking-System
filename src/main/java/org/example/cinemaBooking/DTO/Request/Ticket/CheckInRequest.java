// CheckInRequest.java
package org.example.cinemaBooking.Dto.Request.Ticket;

import jakarta.validation.constraints.NotBlank;

public record CheckInRequest(
        String bookingCode,   // scan QR booking → check-in tất cả ghế
        String ticketCode     // hoặc nhập thủ công từng vé — nullable
) {}