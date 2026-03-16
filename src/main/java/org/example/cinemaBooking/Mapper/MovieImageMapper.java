package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.Entity.MovieImage;
import org.example.cinemaBooking.Model.Response.MovieImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface MovieImageMapper {

    @Mapping(source = "movie.id", target = "id")
    MovieImageResponse toResponse(MovieImage movieImage);

}