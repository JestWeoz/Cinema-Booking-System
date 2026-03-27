package org.example.cinemaBooking.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.DTO.Request.Seat.LockSeatRequest;
import org.example.cinemaBooking.DTO.Request.Seat.UnlockSeatRequest;
import org.example.cinemaBooking.DTO.Response.Showtime.SeatMapResponse;
import org.example.cinemaBooking.DTO.Response.Showtime.ShowtimeSeatResponse;
import org.example.cinemaBooking.Service.Showtime.ShowTImeSeatService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(ApiPaths.API_V1 + ApiPaths.Showtime.BASE + "/{showtimeId}" + ApiPaths.SEAT.BASE)
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ShowtimeSeatController {

    ShowTImeSeatService showTImeSeatService;

    @GetMapping
    ApiResponse<SeatMapResponse> getSeatMap(@PathVariable String showtimeId) {
        SeatMapResponse response = showTImeSeatService.getSeatMap(showtimeId);
        return ApiResponse.<SeatMapResponse>builder()
                .success(true)
                .data(response)
                .message("Lấy sơ đồ ghế thành công")
                .build();
    }

    @GetMapping("/my-locked-seats")
    @PreAuthorize("isAuthenticated()")
    ApiResponse<List<ShowtimeSeatResponse>> getMyLockedSeats(@PathVariable String showtimeId){
        var response = showTImeSeatService.getMyLockedSeats(showtimeId);
        return ApiResponse.<List<ShowtimeSeatResponse>>builder()
                .success(true)
                .data(response)
                .message("Lấy danh sách ghế đang giữ thành công")
                .build();
    }

    @PostMapping("/lock")
    @PreAuthorize("isAuthenticated()")
    ApiResponse<List<ShowtimeSeatResponse>> lockSeats(@PathVariable String showtimeId,
                                                      @RequestBody @Valid LockSeatRequest lockSeatRequest){
        var response = showTImeSeatService.lockSeats(showtimeId, lockSeatRequest);
        return ApiResponse.<List<ShowtimeSeatResponse>>builder()
                .success(true)
                .data(response)
                .message("Giữ chỗ thành công")
                .build();
    }

    @PostMapping("/unlock")
    @PreAuthorize("isAuthenticated()")
    ApiResponse<List<ShowtimeSeatResponse>> unlockSeats(@PathVariable String showtimeId,
                                 @RequestBody @Valid UnlockSeatRequest unlockSeatRequest){
        var response = showTImeSeatService.unlockSeats(showtimeId, unlockSeatRequest);
        return ApiResponse.<List<ShowtimeSeatResponse>>builder()
                .success(true)
                .data(response)
                .message("Huỷ giữ chỗ thành công")
                .build();
    }
}
