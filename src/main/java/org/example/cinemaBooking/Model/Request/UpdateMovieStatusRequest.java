package org.example.cinemaBooking.Model.Request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.cinemaBooking.Shared.contraints.EnumValidator;
import org.example.cinemaBooking.Shared.utils.MovieStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateMovieStatusRequest {
    @EnumValidator(enumClass = MovieStatus.class, message = "MOVIE_STATUS_INVALID")
    String status;
}
