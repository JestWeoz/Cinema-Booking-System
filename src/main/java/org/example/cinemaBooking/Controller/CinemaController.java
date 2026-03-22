package org.example.cinemaBooking.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Request.Cinema.CreateCinemaRequest;
import org.example.cinemaBooking.Dto.Request.Cinema.UpdateCinemaRequest;
import org.example.cinemaBooking.Dto.Response.Cinema.CinemaMovieResponse;
import org.example.cinemaBooking.Dto.Response.Cinema.CinemaResponse;
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
            @RequestParam(defaultValue = "0") int page,
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

    @GetMapping({"/{id}/movies"})
    public ApiResponse<List<CinemaMovieResponse>> getMoviesByCinemaAndDate(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<CinemaMovieResponse> movies = cinemaService.getMoviesByCinemaAndDate(id, date);
        return ApiResponse.<List<CinemaMovieResponse>>builder()
                .success(true)
                .message("Movies retrieved successfully")
                .data(movies)
                .build();
    }


}