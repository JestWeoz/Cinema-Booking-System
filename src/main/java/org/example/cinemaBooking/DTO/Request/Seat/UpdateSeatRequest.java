package org.example.cinemaBooking.DTO.Request.Seat;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateSeatRequest {
    String seatRow;
    Integer seatNumber;
    Long seatTypeId;
}
