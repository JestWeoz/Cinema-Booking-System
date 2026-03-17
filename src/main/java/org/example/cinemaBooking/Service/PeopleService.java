package org.example.cinemaBooking.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Request.AddPeopleToMovieRequest;
import org.example.cinemaBooking.Dto.Request.CreatePeopleRequest;
import org.example.cinemaBooking.Dto.Request.UpdatePeopleRequest;
import org.example.cinemaBooking.Dto.Response.MovieCastResponse;
import org.example.cinemaBooking.Dto.Response.MoviePeopleResponse;
import org.example.cinemaBooking.Dto.Response.PeopleResponse;
import org.example.cinemaBooking.Entity.Movie;
import org.example.cinemaBooking.Entity.MoviePeople;
import org.example.cinemaBooking.Entity.People;
import org.example.cinemaBooking.Exception.AppException;
import org.example.cinemaBooking.Exception.ErrorCode;
import org.example.cinemaBooking.Mapper.MoviePeopleMapper;
import org.example.cinemaBooking.Mapper.PeopleMapper;
import org.example.cinemaBooking.Repository.MoviePeopleRepository;
import org.example.cinemaBooking.Repository.MovieRepository;
import org.example.cinemaBooking.Repository.PeopleRepository;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.example.cinemaBooking.Shared.utils.MovieRole;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PeopleService {
    PeopleRepository peopleRepository;
    MoviePeopleRepository moviePeopleRepository;
    MovieRepository movieRepository;
    PeopleMapper peopleMapper;
    MoviePeopleMapper moviePeopleMapper;
    public PeopleResponse createPeople(CreatePeopleRequest request) {
        var people = peopleMapper.toEntity(request);
        var savedPeople = peopleRepository.save(people);
        log.info("[PEOPLE_SERVICE] - Create people with id: {}", savedPeople.getId());
        return peopleMapper.toResponse(savedPeople);
    }

    public PeopleResponse updatePeople(String id, UpdatePeopleRequest request) {
        var people = peopleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PEOPLE_NOT_FOUND));
        peopleMapper.updatePeople(request, people);
        var updatedPeople = peopleRepository.save(people);
        log.info("[PEOPLE_SERVICE] - Update people with id: {}", updatedPeople.getId());
        return peopleMapper.toResponse(updatedPeople);
    }

    @Transactional
    public void deletePeople(String id){
        var people = peopleRepository.findById(id).orElseThrow(()
                -> new AppException(ErrorCode.PEOPLE_NOT_FOUND));
        moviePeopleRepository.deleteByPeopleId(id);
        // Xóa People
        peopleRepository.delete(people);
        log.info("[PEOPLE_SERVICE] - Delete people with id: {}", id);
    }

    public PeopleResponse getPeopleById(String id) {
        var people = peopleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PEOPLE_NOT_FOUND));
        log.info("[PEOPLE_SERVICE] - Get people with id: {}", id);
        return peopleMapper.toResponse(people);
    }

    public PageResponse<PeopleResponse> getAllPeoples(int page, int size, String key) {
        int pageNumber = page > 0 ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by("name").ascending());
        Page<People> peoplePage =
                (key == null || key.isBlank())
                        ? peopleRepository.findAll(pageable)
                        : peopleRepository.findByNameContainingIgnoreCase(key, pageable);

        List<PeopleResponse> peopleResponses = peoplePage.getContent().stream()
                .map(peopleMapper::toResponse)
                .toList();
        log.info("[PEOPLE_SERVICE] - Get all peoples with page: {}, size: {}, key: {}", page, size, key);
        return PageResponse.<PeopleResponse>builder()
                .page(page)
                .size(size)
                .totalElements(peoplePage.getTotalElements())
                .totalPages(peoplePage.getTotalPages())
                .items(peopleResponses)
                .build();

    }

    @Transactional
    public void addPeopleToMovie(String movieId, String peopleId, AddPeopleToMovieRequest request) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(()
                -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        People people = peopleRepository.findById(peopleId).orElseThrow(()
                -> new AppException(ErrorCode.PEOPLE_NOT_FOUND));


        MoviePeople moviePeople = MoviePeople.builder()
                .movie(movie)
                .people(people)
                .movieRole(MovieRole.valueOf(request.role()))
                .build();

        try {
            moviePeopleRepository.save(moviePeople);
        } catch (DataIntegrityViolationException e) {
            // DB UNIQUE constraint đảm bảo không bị duplicate
            throw new AppException(ErrorCode.PEOPLE_IS_IN_MOVIE);
        }
        log.info("[PEOPLE_SERVICE] - Add people with id: {} to movie with id: {} with role: {}", people.getId(), movie.getId(), request.role());
    }

    @Transactional
    public void removePeopleFromMovie(String movieId, String peopleId) {
        MoviePeople moviePeople = moviePeopleRepository.findByMovieIdAndPeopleId(movieId, peopleId).orElseThrow(()
                -> new AppException(ErrorCode.MOVIE_PEOPLE_NOT_FOUND));
        moviePeopleRepository.delete(moviePeople);
        log.info("[PEOPLE_SERVICE] - Remove people with id: {} from movie with id: {}", peopleId, movieId);
    }

    public  List<MoviePeopleResponse> getMovieByPeople(String peopleId) {
        if(!peopleRepository.existsById(peopleId)){
            throw new AppException(ErrorCode.PEOPLE_NOT_FOUND);
        }
        List<MoviePeople> moviePeoples = moviePeopleRepository.findByPeopleId(peopleId);
        log.info("[PEOPLE_SERVICE] - Get movies by people with id: {}", peopleId);
        return moviePeoples.stream()
                .map(moviePeopleMapper::toMoviePeopleResponse)
                .toList();
    }


    public List<MovieCastResponse> getPeopleByMovie(String movieId) {

        if (!movieRepository.existsById(movieId)) {
            throw new AppException(ErrorCode.MOVIE_NOT_FOUND);
        }

        List<MoviePeople> moviePeoples = moviePeopleRepository.findByMovieId(movieId);

        log.info("[PEOPLE_SERVICE] - Get peoples by movie with id: {}", movieId);

        return moviePeoples.stream()
                .map(moviePeopleMapper::toMovieCastResponse)
                .toList();
    }
}
