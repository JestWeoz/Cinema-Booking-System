package org.example.cinemaBooking.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Request.Showtime.CreateShowtimeRequest;
import org.example.cinemaBooking.Dto.Request.Showtime.ShowtimeFilterRequest;
import org.example.cinemaBooking.Dto.Request.Showtime.UpdateShowtimeRequest;
import org.example.cinemaBooking.Dto.Response.Showtime.ShowtimeDetailResponse;
import org.example.cinemaBooking.Dto.Response.Showtime.ShowtimeSummaryResponse;
import org.example.cinemaBooking.Service.Showtime.ShowtimeService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(ApiPaths.API_V1 + ApiPaths.Showtime.BASE)
public class ShowtimeController {
    ShowtimeService showtimeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ShowtimeDetailResponse> createShowtime(@RequestBody @Valid CreateShowtimeRequest request) {
        ShowtimeDetailResponse response = showtimeService.createShowtime(request);
        log.info("[SHOWTIME_CONTROLLER] Created showtime with id: {}", response.id());
        return ApiResponse.<ShowtimeDetailResponse>builder().
                success(true).message("Showtime created successfully")
                .data(response).
                build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ShowtimeDetailResponse> updateShowtime(@PathVariable String id, @RequestBody @Valid UpdateShowtimeRequest request) {
        var response = showtimeService.updateShowtime(id, request);
        log.info("[SHOWTIME_CONTROLLER] Updated showtime with id: {}", response.id());
        return ApiResponse.<ShowtimeDetailResponse>builder()
                .success(true)
                .success(true).message("Showtime updated successfully")
                .data(response).
                build();
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ShowtimeDetailResponse> cancelShowtime(@PathVariable String id) {
        var response = showtimeService.cancelShowtime(id);
        log.info("[SHOWTIME_CONTROLLER] Cancelled showtime with id: {}", response.id());
        return ApiResponse.<ShowtimeDetailResponse>builder()
                .success(true)
                .message("Showtime cancelled successfully")
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteShowtime(@PathVariable String id) {
        showtimeService.deleteShowtime(id);
        log.info("[SHOWTIME_CONTROLLER] Deleted showtime with id: {}", id);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Showtime deleted successfully")
                .build();
    }


    @GetMapping("/{id}")
    public ApiResponse<ShowtimeDetailResponse> getShowtimeById(@PathVariable String id) {
        var response = showtimeService.getShowtimeById(id);
        log.info("[SHOWTIME_CONTROLLER] Retrieved showtime with id: {}", id);
        return ApiResponse.<ShowtimeDetailResponse>builder()
                .success(true)
                .message("Showtime retrieved successfully")
                .data(response)
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<ShowtimeSummaryResponse>> getShowtimes(
            @Valid ShowtimeFilterRequest request
            ) {
        var response = showtimeService.getShowtimes(request);
        log.info("[SHOWTIME_CONTROLLER] Retrieved showtimes with filters: cinemaId={}, movieId={}, date={}", request.cinemaId(), request.movieId(), request.date());
        return ApiResponse.<PageResponse<ShowtimeSummaryResponse>>builder()
                .success(true)
                .message("Showtimes retrieved successfully")
                .data(response)
                .build();
    }

    /**
     * GET /api/v1/showtimes/by-movie/{movieId}?date=2025-08-01
     * Dùng cho màn hình chọn suất chiếu theo phim.
     */
    @GetMapping("/by-movie/{movieId}")
    public ApiResponse<List<ShowtimeSummaryResponse>> getShowtimesByMovieAndDate(
            @PathVariable String movieId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        var response = showtimeService.getShowtimeByMovieAndDate(movieId, date);
        log.info("[SHOWTIME_CONTROLLER] Retrieved showtimes for movieId={} on date={}", movieId, date);
        return ApiResponse.<List<ShowtimeSummaryResponse>>builder()
                .success(true)
                .message("Showtimes retrieved successfully")
                .data(response)
                .build();
    }

    /**
     * GET /api/v1/showtimes/by-cinema/{cinemaId}?date=2025-08-01
     * Dùng cho màn hình lịch chiếu theo rạp.
     */
    @GetMapping("/by-cinema/{cinemaId}")
    public ApiResponse<List<ShowtimeSummaryResponse>> getShowtimesByCinemaAndDate(
            @PathVariable String cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var response = showtimeService.getShowtimesByCinemaAndDate(cinemaId, date);
        log.info("[SHOWTIME_CONTROLLER] Retrieved showtimes for cinemaId={} on date={}", cinemaId, date);
        return ApiResponse.<List<ShowtimeSummaryResponse>>builder()
                .success(true)
                .message("Showtimes retrieved successfully")
                .data(response)
                .build();
    }
}
