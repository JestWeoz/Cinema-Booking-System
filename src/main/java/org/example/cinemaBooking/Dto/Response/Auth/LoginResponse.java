package org.example.cinemaBooking.Dto.Response.Auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.cinemaBooking.Dto.Response.User.UserInfoResponse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    String AccessToken;
    UserInfoResponse userInfoResponse;
    String error;
    boolean success;
}
