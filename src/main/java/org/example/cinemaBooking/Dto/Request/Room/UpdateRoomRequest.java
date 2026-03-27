package org.example.cinemaBooking.Dto.Request.Room;

import org.example.cinemaBooking.Shared.enums.RoomType;

public record UpdateRoomRequest(
        String name,
        RoomType roomType,
        Integer totalSeats) {
}
