package org.example.cinemaBooking.Dto.Request;

public record UpdateCinemaRequest(
        String name,
        String address,
        String phone,
        String hotline,
        String logoUrl
) {}