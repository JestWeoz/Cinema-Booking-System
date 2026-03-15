package org.example.cinemaBooking.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Entity.Movie;
import org.example.cinemaBooking.Model.Request.CreateMovieRequest;
import org.example.cinemaBooking.Model.Request.UpdateMovieRequest;
import org.example.cinemaBooking.Model.Request.UpdateMovieStatusRequest;
import org.example.cinemaBooking.Model.Response.MovieResponse;
import org.example.cinemaBooking.Service.MovieService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.example.cinemaBooking.Shared.utils.AgeRating;
import org.example.cinemaBooking.Shared.utils.MovieStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(ApiPaths.API_V1 + ApiPaths.Movie.BASE)
public class MovieController {
    MovieService movieService;

    @PostMapping
    public ApiResponse<MovieResponse> createMovie(@RequestBody @Valid CreateMovieRequest request) {
        MovieResponse movieResponse = movieService.creatMovie(request);
        log.info("[MOVIE CONTROLLER] Movie {} has been created", movieResponse.getId());
        return ApiResponse.<MovieResponse>builder()
                .success(true)
                .data(movieResponse)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<MovieResponse> updateMovie(@PathVariable String id, @RequestBody @Valid UpdateMovieRequest request) {
        MovieResponse movieResponse = movieService.updateMovie(id, request);
        log.info("[MOVIE CONTROLLER] Movie {} has been updated", movieResponse.getId());
        return ApiResponse.<MovieResponse>builder()
                .success(true)
                .data(movieResponse)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMovie(@PathVariable String id) {
        movieService.deleteMovie(id);
        log.info("[MOVIE CONTROLLER] Movie {} has been deleted", id);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Movie deleted successfully")
                .build();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<MovieResponse> updateMovieStatus(@PathVariable String id, @RequestBody @Valid UpdateMovieStatusRequest request) {
        MovieResponse movieResponse = movieService.updateMovieStatus(id, request);
        log.info("[MOVIE CONTROLLER] Movie {} status has been updated to");
        return ApiResponse.<MovieResponse>builder()
                .success(true)
                .data(movieResponse)
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<MovieResponse>> getMovies(

            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MovieStatus status,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) AgeRating ageRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "releaseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {

        PageResponse<MovieResponse> movies = movieService.getMovies(
                keyword,
                status,
                categoryId,
                ageRating,
                page,
                size,
                sortBy,
                sortDir
        );

        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .success(true)
                .data(movies)
                .build();
    }
}
