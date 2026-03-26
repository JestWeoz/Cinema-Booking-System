package org.example.cinemaBooking.DTO.Request.Showtime;

import jakarta.validation.constraints.*;
import org.example.cinemaBooking.Shared.utils.Language;
import org.example.cinemaBooking.Shared.utils.ShowTimeStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request cập nhật suất chiếu (partial — chỉ các trường không null mới được apply).
 * MapStruct dùng NullValuePropertyMappingStrategy.IGNORE để bỏ qua null.
 */
public record UpdateShowtimeRequest(

        // Cho phép đổi phòng / giờ khi suất vẫn còn SCHEDULED
        String roomId,

        @Future(message = "startTime must be in the future")
        LocalDateTime startTime,

        @DecimalMin(value = "0.0", inclusive = false, message = "basePrice must be > 0")
        @Digits(integer = 8, fraction = 2)
        BigDecimal basePrice,

        Language language,

        // Admin có thể chủ động set CANCELLED
        ShowTimeStatus status
) {}