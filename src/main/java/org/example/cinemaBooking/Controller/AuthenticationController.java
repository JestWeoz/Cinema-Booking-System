package org.example.cinemaBooking.Controller;


import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.cinemaBooking.Model.Request.LoginRequest;
import org.example.cinemaBooking.Model.Request.LogoutRequest;
import org.example.cinemaBooking.Model.Request.RefreshRequest;
import org.example.cinemaBooking.Model.Request.RegisterRequest;
import org.example.cinemaBooking.Model.Response.LoginResponse;
import org.example.cinemaBooking.Model.Response.RefreshResponse;
import org.example.cinemaBooking.Model.Response.RegisterResponse;
import org.example.cinemaBooking.Service.AuthService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping( ApiPaths.API_V1 + ApiPaths.Auth.BASE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthService authService;

    //API Register
    @PostMapping(ApiPaths.Auth.REGISTER)
    public ApiResponse<RegisterResponse> registerUser(@RequestBody RegisterRequest registerRequest) {
        return ApiResponse.<RegisterResponse>builder().data(authService.registerUser(registerRequest)).build();
    }
    //API Login
    @PostMapping(ApiPaths.Auth.LOGIN)
    public ApiResponse<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        return ApiResponse.<LoginResponse>builder().data(authService.loginUser(loginRequest)).build();
    }

    //API Refresh
    @PostMapping(ApiPaths.Auth.REFRESH)
    public ApiResponse<RefreshResponse> refreshUser(@RequestBody RefreshRequest refreshRequest) throws ParseException, JOSEException {
            return ApiResponse.<RefreshResponse>builder()
                    .data(authService.refreshToken(refreshRequest))
                    .build();
    }
    //API Logout
    @PostMapping(ApiPaths.Auth.LOGOUT)
    public ApiResponse<String> logoutUser(@RequestBody LogoutRequest logoutRequest) throws ParseException, JOSEException {
        authService.logout(logoutRequest);
        return ApiResponse.<String>builder()
                .data("OKE")
                .build();
    }



}
