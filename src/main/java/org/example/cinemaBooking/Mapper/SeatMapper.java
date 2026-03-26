package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.DTO.Response.Seat.SeatResponse;
import org.example.cinemaBooking.Entity.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    @Mapping(target = "seatType", source = "seatType.name")
    @Mapping(target = "roomId", source = "room.id")
    SeatResponse toResponse(Seat seat);
}