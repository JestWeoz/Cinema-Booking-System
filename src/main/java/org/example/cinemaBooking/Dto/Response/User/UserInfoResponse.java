package org.example.cinemaBooking.Dto.Response.User;

import lombok.Data;

@Data
public class UserInfoResponse {
    String username;
    String fullName;
    String email;
    String phone;
}
