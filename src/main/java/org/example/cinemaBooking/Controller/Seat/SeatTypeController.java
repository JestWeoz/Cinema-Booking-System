package org.example.cinemaBooking.Controller.Seat;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.example.cinemaBooking.DTO.Request.Seat.CreateSeatTypeRequest;
import org.example.cinemaBooking.DTO.Request.Seat.UpdateSeatTypeRequest;
import org.example.cinemaBooking.DTO.Response.Seat.SeatTypeResponse;
import org.example.cinemaBooking.Service.Seat.SeatTypeService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.API_V1 + ApiPaths.SEAT.SEAT_TYPE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SeatTypeController {

    SeatTypeService service;

    // POST /api/v1/seat_type
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SeatTypeResponse> create(@RequestBody @Valid CreateSeatTypeRequest request) {
        SeatTypeResponse response = service.create(request);
        log.info("[SEAT_TYPE_CONTROLLER] Created seat type: {}", response.getName());
        return ApiResponse.<SeatTypeResponse>builder()
                .success(true)
                .message("Seat type created successfully")
                .data(response)
                .build();
    }

    // GET /api/v1/seat_type?page=1&size=10
    @GetMapping
    public ApiResponse<PageResponse<SeatTypeResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<SeatTypeResponse>>builder()
                .success(true)
                .message("Seat types retrieved successfully")
                .data(service.getAll(page, size))
                .build();
    }

    // GET /api/v1/seat_type/{id}
    @GetMapping("/{id}")
    public ApiResponse<SeatTypeResponse> getById(@PathVariable String id) {
        SeatTypeResponse response = service.getById(id);
        log.info("[SEAT_TYPE_CONTROLLER] Retrieved seat type: {}", id);
        return ApiResponse.<SeatTypeResponse>builder()
                .success(true)
                .message("Seat type retrieved successfully")
                .data(response)
                .build();
    }

    // PUT /api/v1/seat_type/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SeatTypeResponse> update(@PathVariable String id,
                                                @RequestBody @Valid UpdateSeatTypeRequest request) {
        SeatTypeResponse response = service.update(id, request);
        log.info("[SEAT_TYPE_CONTROLLER] Updated seat type: {}", id);
        return ApiResponse.<SeatTypeResponse>builder()
                .success(true)
                .message("Seat type updated successfully")
                .data(response)
                .build();
    }

    // DELETE /api/v1/seat_type/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable String id) {
        service.delete(id);
        log.info("[SEAT_TYPE_CONTROLLER] Deleted seat type: {}", id);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Seat type deleted successfully")
                .build();
    }
}