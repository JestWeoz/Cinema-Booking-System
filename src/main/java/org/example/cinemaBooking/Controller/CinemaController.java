package org.example.cinemaBooking.Controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Request.CreateCinemaRequest;
import org.example.cinemaBooking.Dto.Response.CinemaResponse;
import org.example.cinemaBooking.Service.CinemaService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(ApiPaths.API_V1 + ApiPaths.Cinema.BASE)
public class CinemaController {
    CinemaService cinemaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CinemaResponse> createCinema(@RequestBody CreateCinemaRequest request) {
        return ApiResponse.<CinemaResponse>builder()
                .success(true)
                .message("Create cinema successfully")
                .data(cinemaService.createCinema(request))
                .build();
    }


}
