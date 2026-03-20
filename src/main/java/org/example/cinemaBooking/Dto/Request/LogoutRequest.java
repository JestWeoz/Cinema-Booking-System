package org.example.cinemaBooking.Dto.Request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LogoutRequest {
    String token;
}
