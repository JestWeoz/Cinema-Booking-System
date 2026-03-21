package org.example.cinemaBooking.Dto.Request;

import jakarta.validation.constraints.NotBlank;

public record CreateCinemaRequest(
        @NotBlank String name,
        String address,
        String phone,
        String hotline,
        String logoUrl
) {}