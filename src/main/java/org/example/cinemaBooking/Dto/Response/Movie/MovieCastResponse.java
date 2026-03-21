package org.example.cinemaBooking.Dto.Response;

import org.example.cinemaBooking.Shared.utils.MovieRole;

public record MovieCastResponse(
        String peopleId,
        String name,
        String avatarUrl,
        MovieRole movieRole
) {}