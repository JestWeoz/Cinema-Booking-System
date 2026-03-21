package org.example.cinemaBooking.Dto.Request.Auth;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LogoutRequest {
    String token;
}
