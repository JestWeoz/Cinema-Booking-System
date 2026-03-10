package org.example.cinemaBooking.Controller;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.cinemaBooking.Model.Request.LoginRequest;
import org.example.cinemaBooking.Model.Request.RegisterRequest;
import org.example.cinemaBooking.Model.Response.LoginResponse;
import org.example.cinemaBooking.Model.Response.RegisterResponse;
import org.example.cinemaBooking.Service.AuthService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( ApiPaths.API_V1 + ApiPaths.AUTHENTICATION)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthService authService;

    //API Register
    @PostMapping(ApiPaths.USERS)
    public ApiResponse<RegisterResponse> registerUser(@RequestBody RegisterRequest registerRequest) {
        return ApiResponse.<RegisterResponse>builder().data(authService.registerUser(registerRequest)).build();
    }
    //API Login
    @GetMapping(ApiPaths.USERS)
    public ApiResponse<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        return ApiResponse.<LoginResponse>builder().data(authService.loginUser(loginRequest)).build();
    }



}
