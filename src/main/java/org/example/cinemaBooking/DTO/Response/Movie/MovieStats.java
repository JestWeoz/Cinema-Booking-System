package org.example.cinemaBooking.DTO.Response.Movie;

public record MovieStats(
        Long id,
        String title,
        String posterUrl,
        Double revenue,
        Long ticketCount,
        Double rating
) {}