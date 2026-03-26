package org.example.cinemaBooking.DTO.Response.Seat;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.cinemaBooking.Shared.utils.SeatTypeEnum;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatTypeResponse {
    String seatTypeId;
    SeatTypeEnum name;
    BigDecimal priceModifier;
}