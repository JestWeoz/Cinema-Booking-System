package org.example.cinemaBooking.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMovieImageRequest {
    @NotNull(message = "IMAGE_URL_IS_REQUIRED")
    List<String > imageUrls;
}
