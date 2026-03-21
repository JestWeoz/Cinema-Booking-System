package org.example.cinemaBooking.Dto.Request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.cinemaBooking.Shared.utils.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCinemaRequest {
    String name;
    String address;
    String phone;
    String hotline;
    String logoUrl;

}