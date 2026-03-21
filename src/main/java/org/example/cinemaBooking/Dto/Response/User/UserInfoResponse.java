package org.example.cinemaBooking.Dto.Response;

import lombok.Data;

@Data
public class UserInfoResponse {
    String username;
    String fullName;
    String email;
    String phone;
}
