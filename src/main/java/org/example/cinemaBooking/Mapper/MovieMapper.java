package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.Entity.Movie;
import org.example.cinemaBooking.Entity.Category;
import org.example.cinemaBooking.Dto.Request.CreateMovieRequest;
import org.example.cinemaBooking.Dto.Request.UpdateMovieRequest;
import org.example.cinemaBooking.Dto.Response.MovieResponse;
import org.example.cinemaBooking.Dto.Response.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    // DTO -> Entity
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "moviePeoples", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "showtimes", ignore = true)
    Movie toMovie(CreateMovieRequest request);

    @Mapping(target = "categories", ignore = true)
    void updateMovie(@MappingTarget Movie movie, UpdateMovieRequest request);

    // Entity -> Response
    MovieResponse toMovieResponse(Movie movie);

    // Category -> CategoryResponse
    CategoryResponse toCategoryResponse(Category category);
}