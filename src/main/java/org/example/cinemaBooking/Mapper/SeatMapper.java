package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.Dto.Request.Seat.UpdateSeatRequest;
import org.example.cinemaBooking.Dto.Response.Seat.SeatResponse;
import org.example.cinemaBooking.Entity.Seat;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "seatTypeId", source = "seatType.id")
    @Mapping(target = "seatTypeName", source = "seatType.name")
    @Mapping(target = "priceModifier", source = "seatType.priceModifier")
    SeatResponse toResponse(Seat seat);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSeat(@MappingTarget Seat seat, UpdateSeatRequest updateSeatRequest);
}