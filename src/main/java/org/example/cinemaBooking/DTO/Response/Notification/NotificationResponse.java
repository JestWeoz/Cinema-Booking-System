package org.example.cinemaBooking.Dto.Response.Notification;

import org.example.cinemaBooking.Shared.enums.Type;

import java.time.LocalDateTime;


public record NotificationResponse(
        String notificationId,
        String title,
        String body,
        Type type,
        boolean read,
        LocalDateTime createdAt
) {
}