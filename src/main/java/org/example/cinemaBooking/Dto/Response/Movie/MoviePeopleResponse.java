package org.example.cinemaBooking.Dto.Response.Movie;

import org.example.cinemaBooking.Shared.utils.MovieRole;

public record MoviePeopleResponse(
        String id,
        String movieId,
        String movieTitle,
        String peopleId,        // ID của người
        String peopleName,      // Tên người
        String peopleAvatar,    // Avatar người
        MovieRole role

) {
}