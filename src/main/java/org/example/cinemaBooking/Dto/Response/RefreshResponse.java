package org.example.cinemaBooking.Model.Response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data

public class RefreshResponse {
    Boolean success;
    String accessToken;
}
