package org.example.cinemaBooking.Controller.Seat;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.example.cinemaBooking.DTO.Request.Seat.CreateSeatRequest;
import org.example.cinemaBooking.DTO.Request.Seat.UpdateSeatRequest;
import org.example.cinemaBooking.DTO.Response.Seat.SeatResponse;
import org.example.cinemaBooking.Service.Seat.SeatService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.API_V1 + ApiPaths.SEAT.BASE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SeatController {

    SeatService service;

    // POST /api/v1/seats
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SeatResponse> create(@RequestBody @Valid CreateSeatRequest request) {
        SeatResponse response = service.create(request);
        log.info("[SEAT_CONTROLLER] Created seat: row={}, number={}", response.getSeatRow(), response.getSeatNumber());
        return ApiResponse.<SeatResponse>builder()
                .success(true)
                .message("Seat created successfully")
                .data(response)
                .build();
    }

    // GET /api/v1/seats?page=1&size=10&sortBy=seatRow&direction=asc
    @GetMapping
    public ApiResponse<PageResponse<SeatResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "seatRow") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ApiResponse.<PageResponse<SeatResponse>>builder()
                .success(true)
                .message("Seats retrieved successfully")
                .data(service.getAll(page, size, sortBy, direction))
                .build();
    }

    // GET /api/v1/seats/{id}
    @GetMapping("/{id}")
    public ApiResponse<SeatResponse> getById(@PathVariable String id) {
        SeatResponse response = service.getById(id);
        log.info("[SEAT_CONTROLLER] Retrieved seat: {}", id);
        return ApiResponse.<SeatResponse>builder()
                .success(true)
                .message("Seat retrieved successfully")
                .data(response)
                .build();
    }

    // PUT /api/v1/seats/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SeatResponse> update(@PathVariable String id,
                                            @RequestBody @Valid UpdateSeatRequest request) {
        SeatResponse response = service.update(id, request);
        log.info("[SEAT_CONTROLLER] Updated seat: {}", id);
        return ApiResponse.<SeatResponse>builder()
                .success(true)
                .message("Seat updated successfully")
                .data(response)
                .build();
    }

    // PATCH /api/v1/seats/{id}/toggle-status
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SeatResponse> toggleStatus(@PathVariable String id) {
        SeatResponse response = service.toggleStatus(id);
        log.info("[SEAT_CONTROLLER] Toggled status for seat: {}", id);
        return ApiResponse.<SeatResponse>builder()
                .success(true)
                .message("Seat status toggled successfully")
                .data(response)
                .build();
    }

    // DELETE /api/v1/seats/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable String id) {
        service.delete(id);
        log.info("[SEAT_CONTROLLER] Deleted seat: {}", id);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Seat deleted successfully")
                .build();
    }
}