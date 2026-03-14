package org.example.cinemaBooking.Controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Model.Request.ChangePasswordRequest;
import org.example.cinemaBooking.Model.Request.UpdateProfileRequest;
import org.example.cinemaBooking.Model.Response.UserResponse;
import org.example.cinemaBooking.Service.UserService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(ApiPaths.API_V1 + ApiPaths.User.BASE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping(ApiPaths.User.ME)
    public ApiResponse<UserResponse> getCurrentUser() {
        UserResponse userResponse = userService.getMyInfo();
        log.info("[USER CONTROLLER] Get user info for user: {}", userResponse.getUsername());
        return ApiResponse.<UserResponse>builder()
                .success(true)
                .data(userResponse)
                .build();

    }

    @PutMapping(ApiPaths.User.ME)
    public ApiResponse<UserResponse> updateCurrentUser(@RequestBody @Valid UpdateProfileRequest request) {
        UserResponse userResponse = userService.updateMyInfo(request);
        log.info("[USER CONTROLLER] Update user info for user: {}", userResponse.getUsername());
        return ApiResponse.<UserResponse>builder()
                .success(true)
                .data(userResponse)
                .build();
    }

    @PutMapping(ApiPaths.User.CHANGE_PASSWORD)
    public ApiResponse<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        log.info("[USER CONTROLLER] Change password for user: {}", request.getOldPassword());
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }


}
