package org.example.cinemaBooking.Dto.Response;

import org.example.cinemaBooking.Shared.utils.Status;

public record CinemaResponse(
        String id,
        String name,
        String address,
        String phone,
        String hotline,
        String logoUrl,
        Status status
) {}