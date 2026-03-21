package org.example.cinemaBooking.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.cinemaBooking.Entity.Cinema;
import org.example.cinemaBooking.Shared.utils.RoomType;
import org.example.cinemaBooking.Shared.utils.Status;
public record CreateRoomRequest(

        @NotBlank(message = "Room name is required")
        String name,

        @NotNull(message = "Total seats is required")
        Integer totalSeats,

        RoomType roomType,

        @NotBlank(message = "CinemaID is required")
        String cinemaId

) {}