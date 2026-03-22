package org.example.cinemaBooking.Dto.Response.Showtime;

import org.example.cinemaBooking.Shared.utils.Language;
import org.example.cinemaBooking.Shared.utils.RoomType;
import org.example.cinemaBooking.Shared.utils.ShowTimeStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Full detail của một suất chiếu — trả về khi user mở trang đặt vé.
 * Bao gồm thêm thông tin rạp, loại phòng, trạng thái computed.
 */
public record ShowtimeDetailResponse(

        String id,

        // ── Movie ──────────────────────────────────────────────────
        String   movieId,
        String movieTitle,
        String posterUrl,
        int    durationMinutes,
        String genre,
        String rating,          // e.g. "PG-13", "T18"

        // ── Room ───────────────────────────────────────────────────
        String    roomId,
        String   roomName,
        RoomType roomType,

        // ── Cinema ─────────────────────────────────────────────────
        Long   cinemaId,
        String cinemaName,
        String cinemaAddress,

        // ── Showtime ───────────────────────────────────────────────
        LocalDateTime  startTime,
        LocalDateTime  endTime,
        BigDecimal     basePrice,
        Language       language,
        ShowTimeStatus status,
        int            availableSeats,

        // ── Computed flags ─────────────────────────────────────────
        boolean bookable,
        boolean ongoing,
        boolean finished
) {}