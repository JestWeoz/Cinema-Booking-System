package org.example.cinemaBooking.Service.Seat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.example.cinemaBooking.DTO.Request.Seat.CreateSeatTypeRequest;
import org.example.cinemaBooking.DTO.Request.Seat.UpdateSeatTypeRequest;
import org.example.cinemaBooking.DTO.Response.Seat.SeatTypeResponse;
import org.example.cinemaBooking.Entity.SeatType;
import org.example.cinemaBooking.Exception.AppException;
import org.example.cinemaBooking.Exception.ErrorCode;
import org.example.cinemaBooking.Mapper.SeatTypeMapper;
import org.example.cinemaBooking.Repository.SeatTypeRepository;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class SeatTypeService {

    SeatTypeRepository repository;
    SeatTypeMapper mapper;

    // CREATE
    public SeatTypeResponse create(CreateSeatTypeRequest request) {
        SeatType seatType = mapper.toEntity(request);
        return mapper.toResponse(repository.save(seatType));
    }

    // GET ALL WITH PAGINATION
    public PageResponse<SeatTypeResponse> getAll(int page, int size) {
        int pageNumber = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by("name").ascending());

        Page<SeatType> seatTypePage = repository.findAll(pageable);
        List<SeatTypeResponse> responses = seatTypePage.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponse.<SeatTypeResponse>builder()
                .page(page)
                .size(size)
                .totalElements(seatTypePage.getTotalElements())
                .totalPages(seatTypePage.getTotalPages())
                .items(responses)
                .build();
    }

    // GET BY ID
    public SeatTypeResponse getById(String id) {
        SeatType seatType = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));
        return mapper.toResponse(seatType);
    }

    // UPDATE
    public SeatTypeResponse update(String id, UpdateSeatTypeRequest request) {
        SeatType seatType = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));

        if (request.getName() != null) {
            seatType.setName(request.getName());
        }
        if (request.getPriceModifier() != null) {
            seatType.setPriceModifier(request.getPriceModifier());
        }

        return mapper.toResponse(repository.save(seatType));
    }

    // DELETE
    @Transactional
    public void delete(String id) {
        SeatType seatType = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));
        repository.delete(seatType);
    }
}