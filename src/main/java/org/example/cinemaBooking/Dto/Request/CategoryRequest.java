package org.example.cinemaBooking.Dto.Request;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank(message = "CATEGORY_NAME_REQUIRED")
        String name
) {
}