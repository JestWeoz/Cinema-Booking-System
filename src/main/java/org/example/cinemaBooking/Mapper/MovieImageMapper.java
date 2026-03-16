package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.Entity.MovieImage;
import org.example.cinemaBooking.Dto.Response.MovieImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovieImageMapper {

    @Mapping(source = "movie.id", target = "id")
    MovieImageResponse toResponse(MovieImage movieImage);

}