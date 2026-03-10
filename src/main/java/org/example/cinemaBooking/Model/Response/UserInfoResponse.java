package org.example.cinemaBooking.Model.Response;

import lombok.Data;

@Data
public class UserInfoResponse {
    String username;
    String fullName;
    String email;
    String phone;
}
