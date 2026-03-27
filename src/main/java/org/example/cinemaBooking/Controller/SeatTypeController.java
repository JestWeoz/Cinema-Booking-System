package org.example.cinemaBooking.Controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.DTO.Response.Seat.SeatTypeResponse;
import org.example.cinemaBooking.Service.Seat.SeatTypeService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(ApiPaths.API_V1 + ApiPaths.SeatType.BASE)
public class SeatTypeController {
    SeatTypeService service;
    @GetMapping
    public ApiResponse<List<SeatTypeResponse>> getAll() {
        return ApiResponse.<List<SeatTypeResponse>>builder()
                .success(true)
                .message("Seat types retrieved successfully")
                .data(service.getAll())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<SeatTypeResponse> getById(@PathVariable String id) {
        return ApiResponse.<SeatTypeResponse>builder()
                .success(true)
                .message("Seat type retrieved successfully")
                .data(service.getById(id))
                .build();
    }
}
