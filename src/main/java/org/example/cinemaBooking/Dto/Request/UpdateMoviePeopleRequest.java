package org.example.cinemaBooking.Dto.Request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UpdateMoviePeopleRequest(
    @NotEmpty(message = "People list cannot be empty")
    List<PeopleRoleRequest> people
) {}