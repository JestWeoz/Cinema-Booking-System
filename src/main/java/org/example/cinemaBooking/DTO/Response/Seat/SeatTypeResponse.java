package org.example.cinemaBooking.Dto.Response.Seat;

import java.math.BigDecimal;

public record SeatTypeResponse(
        String id,
        String name,
        BigDecimal priceModifier
) {}