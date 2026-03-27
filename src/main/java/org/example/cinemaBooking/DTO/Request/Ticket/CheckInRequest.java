// CheckInRequest.java
package org.example.cinemaBooking.DTO.Request.Ticket;

public record CheckInRequest(
        String bookingCode,   // scan QR booking → check-in tất cả ghế
        String ticketCode     // hoặc nhập thủ công từng vé — nullable
) {}