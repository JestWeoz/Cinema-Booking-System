package org.example.cinemaBooking.DTO.Response.Seat;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatResponse {
    String seatId;
    String seatRow;
    Integer seatNumber;
    boolean isActive;
    String seatType;
    String roomId;
}