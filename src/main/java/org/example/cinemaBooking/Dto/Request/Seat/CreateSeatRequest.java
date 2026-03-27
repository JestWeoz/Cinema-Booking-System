package org.example.cinemaBooking.Dto.Request.Seat;

public record CreateSeatRequest(
        String seatRow,
        Integer seatNumber,
        String roomId,
        String seatTypeId
) {}