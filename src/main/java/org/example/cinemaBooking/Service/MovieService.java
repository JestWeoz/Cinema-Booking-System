package org.example.cinemaBooking.Service;

import com.cloudinary.api.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Entity.Category;
import org.example.cinemaBooking.Entity.Movie;
import org.example.cinemaBooking.Exception.AppException;
import org.example.cinemaBooking.Exception.ErrorCode;
import org.example.cinemaBooking.Mapper.CategoryMapper;
import org.example.cinemaBooking.Mapper.MovieMapper;
import org.example.cinemaBooking.Model.Request.CreateMovieRequest;
import org.example.cinemaBooking.Model.Request.UpdateMovieRequest;
import org.example.cinemaBooking.Model.Request.UpdateMovieStatusRequest;
import org.example.cinemaBooking.Model.Response.MovieResponse;
import org.example.cinemaBooking.Model.Response.UserResponse;
import org.example.cinemaBooking.Repository.CategoryRepository;
import org.example.cinemaBooking.Repository.MovieRepository;
import org.example.cinemaBooking.Repository.spefication.MovieSpecification;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.example.cinemaBooking.Shared.utils.AgeRating;
import org.example.cinemaBooking.Shared.utils.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class MovieService {
    MovieRepository movieRepository;
    CategoryRepository categoryRepository;
    MovieMapper movieMapper;

    public MovieResponse creatMovie(CreateMovieRequest request) {
        Movie movie = movieMapper.toMovie(request);
        movie.setAgeRating(AgeRating.valueOf(request.getAgeRating()));

        Set<Category> categories =
                new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));
        movie.setCategories(categories);
        movieRepository.save(movie);
        log.info("[MOVIE SERVICE] Movie {} has been created", movie.getId());
        return movieMapper.toMovieResponse(movie);
    }


    public MovieResponse updateMovie(String id, UpdateMovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        movieMapper.updateMovie(movie, request);
        if (request.getAgeRating() != null) {
            movie.setAgeRating(AgeRating.valueOf(request.getAgeRating()));
        }
        if (request.getCategoryIds() != null) {
            Set<Category> categories =
                    new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));
            movie.setCategories(categories);
            if (categories.size() != request.getCategoryIds().size()) {
                throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
            }
        }
        movieRepository.save(movie);
        log.info("[MOVIE SERVICE] Movie {} has been updated", movie.getId());
        return movieMapper.toMovieResponse(movie);
    }

    public void deleteMovie(String id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        movie.setDeleted(true);
        movieRepository.save(movie);
        log.info("[MOVIE SERVICE] Movie {} has been deleted", movie.getId());
    }

    public MovieResponse getMovieById(String id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        return movieMapper.toMovieResponse(movie);
    }

    public PageResponse<MovieResponse> getMovies(
            String keyword,
            MovieStatus status,
            String categoryId,
            AgeRating ageRating,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Movie> spec =
                MovieSpecification.filterMovie(keyword, status, categoryId, ageRating);

        Page<Movie> movies = movieRepository.findAll(spec, pageable);

        List<MovieResponse> movieResponses = movies.getContent().stream()
                .map(movieMapper::toMovieResponse)
                .toList();
        return PageResponse.<MovieResponse>builder()
                .page(page)
                .size(size)
                .totalElements(movies.getTotalElements())
                .totalPages(movies.getTotalPages())
                .items(movieResponses)
                .build();
    }

    public MovieResponse updateMovieStatus(String id, UpdateMovieStatusRequest request) {
        MovieStatus status = MovieStatus.valueOf(request.getStatus());
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        movie.setStatus(status);
        movieRepository.save(movie);
        log.info("[MOVIE SERVICE] Movie {} status has been updated to {}", movie.getId(), status);
        return movieMapper.toMovieResponse(movie);
    }


}
