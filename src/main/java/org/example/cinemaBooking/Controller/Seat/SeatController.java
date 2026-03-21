package org.example.cinemaBooking.Controller.Seat;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(ApiPaths.API_V1 + ApiPaths.Room.BASE)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class SeatController {
}
