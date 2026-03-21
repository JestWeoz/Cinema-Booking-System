package org.example.cinemaBooking.Service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Request.CreateCinemaRequest;
import org.example.cinemaBooking.Dto.Request.UpdateCinemaRequest;
import org.example.cinemaBooking.Dto.Response.CinemaResponse;
import org.example.cinemaBooking.Entity.Cinema;
import org.example.cinemaBooking.Exception.AppException;
import org.example.cinemaBooking.Exception.ErrorCode;
import org.example.cinemaBooking.Mapper.CinemaMapper;
import org.example.cinemaBooking.Repository.CinemaRepository;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.example.cinemaBooking.Shared.utils.Status;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CinemaService {

    CinemaRepository cinemaRepository;
    CinemaMapper cinemaMapper;

    // ✅ CREATE
    public CinemaResponse createCinema(CreateCinemaRequest request){
        Cinema cinema = cinemaMapper.toCinema(request);
        cinema.setStatus(Status.INACTIVE);
        return cinemaMapper.toResponse(cinemaRepository.save(cinema));
    }

    // ❌ DELETE (soft delete chuẩn)
    public void deleteCinemaById(String id){
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_FOUND));

        if(cinema.getStatus() == Status.INACTIVE){
            throw new AppException(ErrorCode.CINEMA_ALREADY_INACTIVE);
        }

        cinema.setDeleted(true);
        cinemaRepository.save(cinema);
    }

    // 🔍 GET BY ID
    public CinemaResponse getCinemaById(String id){
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_FOUND));

        return cinemaMapper.toResponse(cinema);
    }

    // 🔄 TOGGLE STATUS
    public void toggleCinemaStatus(String id){
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_FOUND));

        if(cinema.getStatus() == Status.ACTIVE){
            cinema.setStatus(Status.INACTIVE);
        } else {
            cinema.setStatus(Status.ACTIVE);
        }

        cinemaRepository.save(cinema);
    }

    // 📄 GET ALL + PAGINATION + SEARCH
    public PageResponse<CinemaResponse> getAllCinemas(
            int page,
            int size,
            String sortBy,
            String direction,
            String keyword
    ){
        int pageNumber = page > 0 ? page - 1 : 0;

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, size, sort);

        Page<Cinema> cinemaPage;

        if(keyword != null && !keyword.isBlank()){
            cinemaPage = cinemaRepository
                    .findByNameContainingIgnoreCaseAndDeletedFalse(keyword, pageable);
        } else {
            cinemaPage = cinemaRepository.findAllByDeletedFalse(pageable);
        }

        return PageResponse.<CinemaResponse>builder()
                .page(page)
                .size(size)
                .totalElements(cinemaPage.getTotalElements())
                .totalPages(cinemaPage.getTotalPages())
                .items(cinemaPage.getContent().stream()
                        .map(cinemaMapper::toResponse)
                        .toList())
                .build();
    }

    // ✏️ UPDATE
    public CinemaResponse updateCinema(String id, UpdateCinemaRequest request){
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_FOUND));

        cinemaMapper.updateCinema(request, cinema);

        return cinemaMapper.toResponse(cinemaRepository.save(cinema));
    }
}