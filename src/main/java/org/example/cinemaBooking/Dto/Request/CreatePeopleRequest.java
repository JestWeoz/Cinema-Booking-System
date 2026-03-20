package org.example.cinemaBooking.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import org.example.cinemaBooking.Entity.People;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link People}
 */
public record CreatePeopleRequest(@NotBlank(message = "NAME_REQUIRED") String name,
                                  @NotBlank(message = "NATION_REQUIRED") String nation,
                                  @NotBlank(message = "IMAGE_URL_NOT_BLANK") String avatarUrl,
                                  LocalDate dob)
        implements Serializable {
}