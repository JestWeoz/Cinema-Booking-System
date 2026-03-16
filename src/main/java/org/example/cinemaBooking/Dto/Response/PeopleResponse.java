package org.example.cinemaBooking.Dto.Response;

import java.time.LocalDate;

public record PeopleResponse(
        String id,
        String name,
        String nation,
        String avatarUrl,
        LocalDate dob
) {}