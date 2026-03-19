package org.example.cinemaBooking.Dto.Response;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReviewResponse(String id,
                             String movieId,
                             String movieTitle,
                             String userId,
                             Integer rating,
                             String comment,
                             String username,
                             LocalDateTime createdAt,
                             LocalDateTime updateAt,
                             boolean deleted) {
}
