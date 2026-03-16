package org.example.cinemaBooking.Dto.Response;

import org.example.cinemaBooking.Shared.utils.MovieRole;

public record MoviePeopleResponse(
        String movieId,
        String movieTitle,
        MovieRole role
) {
}