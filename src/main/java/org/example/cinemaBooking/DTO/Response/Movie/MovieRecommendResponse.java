package org.example.cinemaBooking.DTO.Response.Movie;

public record MovieRecommendResponse(
        Long id,
        String title,
        String posterUrl,
        Double score
) {}