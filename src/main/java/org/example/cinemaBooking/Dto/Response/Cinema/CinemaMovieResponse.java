package org.example.cinemaBooking.Dto.Response.Cinema;

import org.example.cinemaBooking.Shared.utils.Language;

public record CinemaMovieResponse(
        String movieId,
        String movieTitle,
        String posterUrl,
        int durationMinutes,
        Language language
) {}