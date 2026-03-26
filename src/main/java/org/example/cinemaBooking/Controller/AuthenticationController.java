package org.example.cinemaBooking.Controller;


import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.DTO.Request.Auth.*;
import org.example.cinemaBooking.DTO.Request.User.ForgotPasswordRequest;
import org.example.cinemaBooking.DTO.Request.User.RegisterRequest;
import org.example.cinemaBooking.DTO.Response.Auth.AuthResponse;
import org.example.cinemaBooking.DTO.Response.Auth.LoginResponse;
import org.example.cinemaBooking.DTO.Response.User.RegisterResponse;
import org.example.cinemaBooking.Service.Auth.AuthService;
import org.example.cinemaBooking.Service.Auth.PasswordResetService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.example.cinemaBooking.Shared.response.IntrospectResponse;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping( ApiPaths.API_V1 + ApiPaths.Auth.BASE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationController {
    AuthService authService;
    PasswordResetService passwordResetService;
    //API Register
    @PostMapping(ApiPaths.Auth.REGISTER)
    public ApiResponse<RegisterResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest) {
        return ApiResponse.<RegisterResponse>builder()
                .success(true)
                .data(authService.registerUser(registerRequest))
                .build();
    }
    //API Login
    @PostMapping(ApiPaths.Auth.LOGIN)
    public ApiResponse<LoginResponse> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
        return ApiResponse.<LoginResponse>builder()
                .success(true)
                .data(authService.loginUser(loginRequest)).build();
    }

    @PostMapping(ApiPaths.Auth.LOGOUT)
    public ApiResponse<Void> logout(@RequestBody @Valid LogoutRequest request)
            throws ParseException, JOSEException {
        authService.logout(request);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Logged out successfully")
                .build();
    }
    @PostMapping(ApiPaths.Auth.REFRESH)
    public ApiResponse<AuthResponse> refresh(@RequestBody @Valid RefreshRequest request)
            throws ParseException, JOSEException {
        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .data(authService.refreshToken(request))
                .build();
    }

    @PostMapping(ApiPaths.Auth.INTROSPECT)
    public ApiResponse<IntrospectResponse> introspect(@RequestBody @Valid IntrospectReq request) {
        return ApiResponse.<IntrospectResponse>builder()
                .success(true)
                .data(authService.introspect(request))
                .build();
    }

    /**
     * Bước 1: Gửi email reset
     * POST /auth/forgot-password
     * Body: { "email": "user@gmail.com" }
     */
    @PostMapping(ApiPaths.Auth.FORGOT_PASSWORD)
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.forgotPassword(request);
        log.info("[AUTH CONTROLLER] Password reset requested for email: {}", request.getEmail());
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Nếu email tồn tại, link đặt lại mật khẩu đã được gửi.")
                .build();
    }

    /**
     * Bước 1.5: FE kiểm tra token còn hợp lệ không trước khi hiện form
     * GET /auth/reset-password/validate?token=xxx
     */
    @GetMapping(ApiPaths.Auth.RESET_PASSWORD + "/validate")
    public ApiResponse<Boolean> validateToken(@RequestParam String token) {
        boolean valid = passwordResetService.validateToken(token);
        return ApiResponse.<Boolean>builder()
                .data(valid)
                .success(true)
                .message(valid ? "Token hợp lệ" : "Token không hợp lệ hoặc đã hết hạn")
                .build();
    }

    /**
     * Bước 2: Đặt lại mật khẩu
     * POST /auth/reset-password
     * Body: { "token": "xxx", "newPassword": "...", "confirmPassword": "..." }
     */
    @PostMapping(ApiPaths.Auth.RESET_PASSWORD)
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại.")
                .build();
    }
}
