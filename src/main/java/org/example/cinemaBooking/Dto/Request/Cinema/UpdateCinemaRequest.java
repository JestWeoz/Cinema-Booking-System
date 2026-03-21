package org.example.cinemaBooking.Dto.Request.Cinema;

public record UpdateCinemaRequest(
        String name,
        String address,
        String phone,
        String hotline,
        String logoUrl
) {}