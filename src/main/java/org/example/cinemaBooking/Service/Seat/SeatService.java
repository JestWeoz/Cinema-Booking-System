package org.example.cinemaBooking.Service.Seat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.example.cinemaBooking.DTO.Request.Seat.CreateSeatRequest;
import org.example.cinemaBooking.DTO.Request.Seat.UpdateSeatRequest;
import org.example.cinemaBooking.DTO.Response.Seat.SeatResponse;
import org.example.cinemaBooking.Entity.Room;
import org.example.cinemaBooking.Entity.Seat;
import org.example.cinemaBooking.Entity.SeatType;
import org.example.cinemaBooking.Exception.AppException;
import org.example.cinemaBooking.Exception.ErrorCode;
import org.example.cinemaBooking.Mapper.SeatMapper;
import org.example.cinemaBooking.Repository.RoomRepository;
import org.example.cinemaBooking.Repository.SeatRepository;
import org.example.cinemaBooking.Repository.SeatTypeRepository;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SeatService {

    SeatRepository seatRepository;
    SeatTypeRepository seatTypeRepository;
    RoomRepository roomRepository;
    SeatMapper seatMapper;

    // CREATE
    public SeatResponse create(CreateSeatRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        SeatType seatType = seatTypeRepository.findById(request.getSeatTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));

        Seat seat = Seat.builder()
                .seatRow(request.getSeatRow())
                .seatNumber(request.getSeatNumber())
                .room(room)
                .seatType(seatType)
                .build();

        return seatMapper.toResponse(seatRepository.save(seat));
    }

    // GET ALL WITH PAGINATION
    public PageResponse<SeatResponse> getAll(int page, int size, String sortBy, String direction) {
        int pageNumber = Math.max(page - 1, 0);
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, size, sort);

        Page<Seat> seatPage = seatRepository.findAll(pageable);
        List<SeatResponse> responses = seatPage.getContent().stream()
                .map(seatMapper::toResponse)
                .toList();

        return PageResponse.<SeatResponse>builder()
                .page(page)
                .size(size)
                .totalElements(seatPage.getTotalElements())
                .totalPages(seatPage.getTotalPages())
                .items(responses)
                .build();
    }

    // GET BY ID
    public SeatResponse getById(String id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));
        return seatMapper.toResponse(seat);
    }

    // UPDATE
    public SeatResponse update(String id, UpdateSeatRequest request) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));

        if (request.getSeatRow() != null) {
            seat.setSeatRow(request.getSeatRow());
        }
        if (request.getSeatNumber() != null) {
            seat.setSeatNumber(request.getSeatNumber());
        }
        if (request.getSeatTypeId() != null) {
            SeatType seatType = seatTypeRepository.findById(request.getSeatTypeId())
                    .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));
            seat.setSeatType(seatType);
        }

        return seatMapper.toResponse(seatRepository.save(seat));
    }

    // TOGGLE ACTIVE STATUS
    @Transactional
    public SeatResponse toggleStatus(String id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));
        seat.setActive(!seat.isActive());
        return seatMapper.toResponse(seatRepository.save(seat));
    }

    // DELETE
    @Transactional
    public void delete(String id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));
        seatRepository.delete(seat);
    }
}