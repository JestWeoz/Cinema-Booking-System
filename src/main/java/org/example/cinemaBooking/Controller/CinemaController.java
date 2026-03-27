package org.example.cinemaBooking.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Request.Cinema.CreateCinemaRequest;
import org.example.cinemaBooking.Dto.Request.Cinema.UpdateCinemaRequest;
import org.example.cinemaBooking.Dto.Response.Cinema.CinemaMovieResponse;
import org.example.cinemaBooking.Dto.Response.Cinema.CinemaResponse;
import org.example.cinemaBooking.Dto.Response.Room.RoomBasicResponse;
import org.example.cinemaBooking.Service.Cinema.CinemaService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(ApiPaths.API_V1 + ApiPaths.Cinema.BASE)
public class CinemaController {

    CinemaService cinemaService;

    // ✅ CREATE
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<CinemaResponse> createCinema(@RequestBody @Valid CreateCinemaRequest request){
        CinemaResponse response = cinemaService.createCinema(request);
        return ApiResponse.<CinemaResponse>builder()
                .success(true)
                .message("Cinema created successfully")
                .data(response)
                .build();
    }

    // ✏️ UPDATE
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<CinemaResponse> updateCinema(
            @PathVariable String id,
            @RequestBody @Valid UpdateCinemaRequest request
    ){
        return ApiResponse.<CinemaResponse>builder()
                .success(true)
                .message("Cinema updated successfully")
                .data(cinemaService.updateCinema(id, request))
                .build();
    }

    // ❌ DELETE
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCinema(@PathVariable String id){
        cinemaService.deleteCinemaById(id);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Cinema deleted successfully")
                .build();
    }

    // 🔍 GET BY ID
    @GetMapping("/{id}")
    public ApiResponse<CinemaResponse> getCinema(@PathVariable String id){
        return ApiResponse.<CinemaResponse>builder()
                .success(true)
                .message("Cinema retrieved successfully")
                .data(cinemaService.getCinemaById(id))
                .build();
    }

    // 🔄 TOGGLE STATUS
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/toggle-status")
    public ApiResponse<CinemaResponse> toggleStatus(@PathVariable String id){
        cinemaService.toggleCinemaStatus(id);
        return ApiResponse.<CinemaResponse>builder()
                .success(true)
                .message("Cinema status toggled successfully")
                .data(cinemaService.getCinemaById(id))
                .build();
    }

    // 📄 GET ALL
    @GetMapping
    public ApiResponse<PageResponse<CinemaResponse>> getAllCinemas(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword
    ){
        return ApiResponse.<PageResponse<CinemaResponse>>builder()
                .success(true)
                .message("Cinemas retrieved successfully")
                .data(cinemaService.getAllCinemas(page, size, sortBy, direction, keyword))
                .build();
    }

    @GetMapping("/{cinemaId}/movies")
    public ApiResponse<PageResponse<CinemaMovieResponse>> getMoviesByCinemaAndDate(
            @PathVariable String cinemaId,
            @RequestParam LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        log.info("[CINEMA_CONTROLLER] Getting movies for cinema: {}, date: {}, page: {}",
                cinemaId, date, page);

        var movies = cinemaService.getMoviesByCinemaAndDate(
                cinemaId, date, page, size, sortBy, direction);

        return ApiResponse.<PageResponse<CinemaMovieResponse>>builder()
                .success(true)
                .message("Movies retrieved successfully")
                .data(movies)
                .build();
    }

    @GetMapping("/{cinemaId}/rooms")
    public ApiResponse<PageResponse<RoomBasicResponse>> getRoomsByCinemaId(
            @PathVariable String cinemaId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        log.info("[ROOM_CONTROLLER] Getting rooms for cinema id: {} - page: {}, size: {}",
                cinemaId, page, size);

        PageResponse<RoomBasicResponse> response = cinemaService.getRoomsByCinema(
                cinemaId, page, size, sortBy, direction);

        return ApiResponse.<PageResponse<RoomBasicResponse>>builder()
                .success(true)
                .message("Rooms retrieved successfully")
                .data(response)
                .build();
    }

}