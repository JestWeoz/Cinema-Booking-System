package org.example.cinemaBooking.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import org.example.cinemaBooking.Shared.contraints.EnumValidator;
import org.example.cinemaBooking.Shared.utils.MovieRole;

public record AddPeopleToMovieRequest(
        @NotBlank String peopleId,
        @EnumValidator(enumClass = MovieRole.class) String role
){
}