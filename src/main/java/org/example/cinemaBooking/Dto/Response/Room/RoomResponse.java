package org.example.cinemaBooking.Dto.Response;

import org.example.cinemaBooking.Shared.utils.RoomType;
import org.example.cinemaBooking.Shared.utils.Status;

import java.time.LocalDateTime;

public record RoomResponse(

        String id,
        String name,
        String totalSeats,
        RoomType roomType,
        Status status,

        // cinema info (không trả full object)
        String cinemaId,
        String cinemaName,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {}