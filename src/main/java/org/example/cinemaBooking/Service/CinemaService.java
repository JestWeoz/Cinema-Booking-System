package org.example.cinemaBooking.Service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Request.CreateCinemaRequest;
import org.example.cinemaBooking.Dto.Response.CinemaResponse;
import org.example.cinemaBooking.Entity.Cinema;
import org.example.cinemaBooking.Mapper.CinemaMapper;
import org.example.cinemaBooking.Mapper.RoomMapper;
import org.example.cinemaBooking.Repository.CinemaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(
        level = lombok.AccessLevel.PRIVATE,
        makeFinal = true
)
public class CinemaService {
    CinemaMapper cinemaMapper;
    CinemaRepository cinemaRepository;

    public CinemaResponse createCinema(CreateCinemaRequest request) {
        return cinemaMapper.toResponse(cinemaRepository.save(cinemaMapper.toEntity(request)));
    }
}
