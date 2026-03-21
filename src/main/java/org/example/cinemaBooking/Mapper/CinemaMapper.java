package org.example.cinemaBooking.Mapper;


import org.example.cinemaBooking.Dto.Request.CreateCinemaRequest;
import org.example.cinemaBooking.Dto.Response.CinemaResponse;
import org.example.cinemaBooking.Entity.Cinema;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CinemaMapper {
    // Create
    Cinema toEntity(CreateCinemaRequest request);

    // Response
    CinemaResponse toResponse(Cinema cinema);

}
