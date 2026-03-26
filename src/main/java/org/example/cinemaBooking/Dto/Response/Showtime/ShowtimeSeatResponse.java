// ShowtimeSeatResponse.java
package org.example.cinemaBooking.Dto.Response.Showtime;

import org.example.cinemaBooking.Shared.utils.SeatStatus;
import org.example.cinemaBooking.Shared.utils.SeatTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Thông tin 1 ghế trong suất chiếu.
 */
public record ShowtimeSeatResponse(
        String showtimeSeatId,
        String seatId,
        String seatRow,
        Integer seatNumber,
        SeatTypeEnum seatType,
        BigDecimal finalPrice,        // basePrice + priceModifier
        SeatStatus status,
        LocalDateTime lockedUntil,    // null nếu không bị lock
        Long lockedByUser             // null nếu không bị lock
) {}