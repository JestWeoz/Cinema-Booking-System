package org.example.cinemaBooking.Service;

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

    public void deletePeople(String id){
        var people = peopleRepository.findById(id).orElseThrow(()
                -> new AppException(ErrorCode.PEOPLE_NOT_FOUND));
        boolean exists = moviePeopleRepository.existsByPeopleId(people.getId());
        if (exists) {
            throw new AppException(ErrorCode.PEOPLE_IS_IN_MOVIE);
        }
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

    public void addPeopleToMovie(String movieId, AddPeopleToMovieRequest request) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(()
                -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        People people = peopleRepository.findById(request.peopleId()).orElseThrow(()
                -> new AppException(ErrorCode.PEOPLE_NOT_FOUND));

        boolean exists = moviePeopleRepository.existsByMovieIdAndPeopleId(movie.getId(), people.getId());
        if(exists){
            throw new AppException(ErrorCode.PEOPLE_IS_IN_MOVIE);
        }
        MoviePeople moviePeople = MoviePeople.builder()
                .movie(movie)
                .people(people)
                .movieRole(MovieRole.valueOf(request.role()))
                .build();
        moviePeopleRepository.save(moviePeople);
        log.info("[PEOPLE_SERVICE] - Add people with id: {} to movie with id: {} with role: {}", people.getId(), movie.getId(), request.role());
    }

    public  List<MoviePeopleResponse> getMovieByPeople(String peopleId) {
        if(!peopleRepository.existsById(peopleId)){
            throw new AppException(ErrorCode.PEOPLE_NOT_FOUND);
        }
        List<MoviePeople> moviePeoples = moviePeopleRepository.findByPeopleId(peopleId);
        log.info("[PEOPLE_SERVICE] - Get movies by people with id: {}", peopleId);
        return moviePeoples.stream()
                .map(moviePeople -> new MoviePeopleResponse(
                        moviePeople.getMovie().getId(),
                        moviePeople.getMovie().getTitle(),
                        moviePeople.getMovieRole()
                ))
                .toList();
    }

    public List<MovieCastResponse> getPeopleByMovie(String movieId) {

        if (!movieRepository.existsById(movieId)) {
            throw new AppException(ErrorCode.MOVIE_NOT_FOUND);
        }

        List<MoviePeople> moviePeoples = moviePeopleRepository.findByMovieId(movieId);

        log.info("[PEOPLE_SERVICE] - Get peoples by movie with id: {}", movieId);

        return moviePeoples.stream()
                .map(moviePeopleMapper::toResponse)
                .toList();
    }
}
