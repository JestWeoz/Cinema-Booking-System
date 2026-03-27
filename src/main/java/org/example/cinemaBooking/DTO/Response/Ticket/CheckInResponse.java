// CheckInResponse.java
package org.example.cinemaBooking.Dto.Response.Ticket;

import org.example.cinemaBooking.Shared.enums.TicketStatus;

import java.time.LocalDateTime;

public record CheckInResponse(
    String        ticketCode,
    String        movieTitle,
    String        roomName,
    String        seatRow,
    Integer       seatNumber,
    TicketStatus  status,
    LocalDateTime checkedInAt,
    String        message
) {}