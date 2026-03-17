package org.example.cinemaBooking.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import org.example.cinemaBooking.Shared.contraints.EnumValidator;
import org.example.cinemaBooking.Shared.utils.MovieRole;

public record AddPeopleToMovieRequest(
        @NotBlank(message = "ROLE_REQUIRED") @EnumValidator(enumClass = MovieRole.class) String role
){
}