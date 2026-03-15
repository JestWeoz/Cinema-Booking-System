package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.Entity.MovieImage;
import org.example.cinemaBooking.Model.Response.MovieImageResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieImageMapper {

    MovieImageResponse toMovieImageResponse(MovieImage movieImage);

}