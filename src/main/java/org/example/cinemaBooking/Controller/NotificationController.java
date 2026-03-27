package org.example.cinemaBooking.Controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Response.Notification.NotificationResponse;
import org.example.cinemaBooking.Service.Notification.NotificationService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping((ApiPaths.API_V1 + ApiPaths.Notification.BASE))
public class NotificationController {
    NotificationService notificationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PageResponse<NotificationResponse>> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.<PageResponse<NotificationResponse>>builder()
                .success(true)
                .message("Notifications retrieved successfully")
                .data(notificationService.getMyNotifications(page, size))
                .build();
    }

    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Integer> countUnread() {
        return ApiResponse.<Integer>builder()
                .success(true)
                .message("Unread notifications count retrieved successfully")
                .data(notificationService.countUnread())
                .build();
    }

    @PatchMapping("/mark-all-read")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiResponse.<Void>builder()
                .success(true)
                .message("All notifications marked as read successfully")
                .build();
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Notification marked as read successfully")
                .build();
    }
}
