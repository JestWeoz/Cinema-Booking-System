package org.example.cinemaBooking.Dto.Request.Cinema;

import jakarta.validation.constraints.NotBlank;

public record CreateCinemaRequest(
        @NotBlank String name,
        String address,
        String phone,
        String hotline,
        String logoUrl
) {}