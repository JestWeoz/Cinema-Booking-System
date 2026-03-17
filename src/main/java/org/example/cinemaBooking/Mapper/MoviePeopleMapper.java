package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.Dto.Response.MovieCastResponse;
import org.example.cinemaBooking.Dto.Response.MoviePeopleResponse;
import org.example.cinemaBooking.Entity.MoviePeople;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MoviePeopleMapper {
    @Mapping(source = "people.id", target = "peopleId")
    @Mapping(source = "people.name", target = "name")
    @Mapping(source = "people.avatarUrl", target = "avatarUrl")
    MovieCastResponse toMovieCastResponse(MoviePeople moviePeople);

    @Mapping(source = "movie.id", target = "movieId")
    @Mapping(source = "movie.title", target = "movieTitle")
    MoviePeopleResponse toMoviePeopleResponse(MoviePeople moviePeople);

}
