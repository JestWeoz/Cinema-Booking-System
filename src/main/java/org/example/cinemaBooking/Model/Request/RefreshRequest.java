package org.example.cinemaBooking.Model.Request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RefreshRequest {
    String accessToken;
}
