package org.example.cinemaBooking.Controller;

import com.cloudinary.Api;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Model.Request.ChangeAvatarRequest;
import org.example.cinemaBooking.Model.Request.ChangePasswordRequest;
import org.example.cinemaBooking.Model.Request.CreateUserRequest;
import org.example.cinemaBooking.Model.Request.UpdateProfileRequest;
import org.example.cinemaBooking.Model.Response.UserResponse;
import org.example.cinemaBooking.Service.UserService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping(ApiPaths.User.CHANGE_AVATAR)
    public ApiResponse<UserResponse> changeAvatar(@RequestBody @Valid ChangeAvatarRequest request) {
        UserResponse userResponse = userService.changeAvatar(request);
        log.info("[USER CONTROLLER] Change avatar for user: {}", userResponse.getUsername());
        return ApiResponse.<UserResponse>builder()
                .success(true)
                .data(userResponse)
                .build();
    }

    @PutMapping(ApiPaths.User.LOCK + "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> lockUser(@PathVariable String id) {
        userService.lockUser(id);
        log.info("[USER CONTROLLER] Lock user with id: {}", id);
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }

    @PutMapping(ApiPaths.User.UNLOCK + "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> unlockUser(@PathVariable String id) {
        userService.unlockUser(id);
        log.info("[USER CONTROLLER] Unlock user");
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> getUserById(@PathVariable String id) {
        UserResponse userResponse = userService.getUserById(id);
        log.info("[USER CONTROLLER] Get user info for user with id: {}", id);
        return ApiResponse.<UserResponse>builder()
                .success(true)
                .data(userResponse)
                .build();
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> getUserByUsername(@PathVariable String username) {
        UserResponse userResponse = userService.getUserByUsername(username);
        log.info("[USER CONTROLLER] Get user info for user with username: {}", username);
        return ApiResponse.<UserResponse>builder()
                .success(true)
                .data(userResponse)
                .build();
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size,
                                                               @RequestParam(required = false) String key) {
        PageResponse<UserResponse> pageResponse = userService.getALlUser(page, size, key);
        log.info("[USER CONTROLLER] Get all users with page: {}, size: {}, key {}", page, size, key);
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .success(true)
                .data(pageResponse)
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        UserResponse userResponse = userService.createUser(request);
        log.info("[USER CONTROLLER] Create user with username: {}", userResponse.getUsername());
        return ApiResponse.<UserResponse>builder()
                .success(true)
                .data(userResponse)
                .build();
    }
}
