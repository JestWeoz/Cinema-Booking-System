package org.example.cinemaBooking.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        @NotBlank(message = "MOVIE_ID_REQUIRED") String movieId,
        @NotNull(message = "RATING_REQUIRED") Integer rating,
        @NotBlank(message = "COMMENT_REQUIRED") String comment
) {
}