package org.example.cinemaBooking.Dto.Response;

import lombok.Builder;

@Builder
public record CategoryResponse(
        String id,
        String name
) {
}