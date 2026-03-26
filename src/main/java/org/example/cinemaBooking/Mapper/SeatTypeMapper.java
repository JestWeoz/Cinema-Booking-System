package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.DTO.Request.Seat.CreateSeatTypeRequest;
import org.example.cinemaBooking.DTO.Response.Seat.SeatTypeResponse;
import org.example.cinemaBooking.Entity.SeatType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SeatTypeMapper {
    SeatType toEntity(CreateSeatTypeRequest request);
    SeatTypeResponse toResponse(SeatType seatType);
}