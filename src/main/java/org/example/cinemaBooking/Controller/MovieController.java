package org.example.cinemaBooking.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Request.AddPeopleToMovieRequest;
import org.example.cinemaBooking.Dto.Request.CreateMovieRequest;
import org.example.cinemaBooking.Dto.Request.UpdateMovieRequest;
import org.example.cinemaBooking.Dto.Request.UpdateMovieStatusRequest;
import org.example.cinemaBooking.Dto.Response.MovieCastResponse;
import org.example.cinemaBooking.Dto.Response.MovieResponse;
import org.example.cinemaBooking.Service.MovieService;
import org.example.cinemaBooking.Service.PeopleService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.example.cinemaBooking.Shared.utils.AgeRating;
import org.example.cinemaBooking.Shared.utils.MovieStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(ApiPaths.API_V1 + ApiPaths.Movie.BASE)
public class MovieController {
    MovieService movieService;
    PeopleService peopleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MovieResponse> createMovie(@RequestBody @Valid CreateMovieRequest request) {
        MovieResponse movieResponse = movieService.creatMovie(request);
        log.info("[MOVIE CONTROLLER] Movie {} has been created", movieResponse.getId());
        return ApiResponse.<MovieResponse>builder()
                .success(true)
                .data(movieResponse)
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MovieResponse> updateMovie(@PathVariable String id, @RequestBody @Valid UpdateMovieRequest request) {
        MovieResponse movieResponse = movieService.updateMovie(id, request);
        log.info("[MOVIE CONTROLLER] Movie {} has been updated", movieResponse.getId());
        return ApiResponse.<MovieResponse>builder()
                .success(true)
                .data(movieResponse)
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteMovie(@PathVariable String id) {
        movieService.deleteMovie(id);
        log.info("[MOVIE CONTROLLER] Movie {} has been deleted", id);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Movie deleted successfully")
                .build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MovieResponse> updateMovieStatus(@PathVariable String id, @RequestBody @Valid UpdateMovieStatusRequest request) {
        MovieResponse movieResponse = movieService.updateMovieStatus(id, request);
        log.info("[MOVIE CONTROLLER] Movie {} status has been updated to {}", movieResponse.getId(), movieResponse.getStatus());
        return ApiResponse.<MovieResponse>builder()
                .success(true)
                .data(movieResponse)
                .build();
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<MovieResponse>> getMovies(

            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MovieStatus status,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) AgeRating ageRating,
            @RequestParam(defaultValue = "1") int page,
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
        log.info("[MOVIE CONTROLLER] Get movies with keyword: {}, status: {}, categoryId: {}, ageRating: {}, page: {}, size: {}, sortBy: {}, sortDir: {}",
                keyword, status, categoryId, ageRating, page, size, sortBy, sortDir);
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .success(true)
                .data(movies)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<MovieResponse> getMovieDetailById(@PathVariable String id) {
        MovieResponse movieResponse = movieService.getMovieById(id);
        log.info("[MOVIE CONTROLLER] Get movie detail for movie with id: {}", id);
        return ApiResponse.<MovieResponse>builder()
                .success(true)
                .data(movieResponse)
                .build();
    }

    @GetMapping("/slug/{slug}")
    public ApiResponse<MovieResponse> getMovieDetailBySlug(@PathVariable String slug) {
        MovieResponse movieResponse = movieService.getMovieBySlug(slug);
        log.info("[MOVIE CONTROLLER] Get movie detail for movie with slug: {}", slug);
        return ApiResponse.<MovieResponse>builder()
                .success(true)
                .data(movieResponse)
                .build();
    }

    @GetMapping(ApiPaths.Movie.NOW_SHOWING)
    public ApiResponse<PageResponse<MovieResponse>> getNowShowingMovies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size){
        PageResponse<MovieResponse> movies = movieService.getMoviesIsNowShowing(page, size);
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .success(true)
                .data(movies)
                .build();
    }

    @GetMapping(ApiPaths.Movie.COMING_SOON)
    public ApiResponse<PageResponse<MovieResponse>> getComingSoonMovies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<MovieResponse> movies = movieService.getMoviesIsComingSoon(page, size);
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .success(true)
                .data(movies)
                .build();
    }

    @GetMapping(ApiPaths.Movie.SEARCH + "/{keyword}")
    public ApiResponse<PageResponse<MovieResponse>> searchMovies(
            @PathVariable String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<MovieResponse> movies = movieService.searchMovie(page, size, keyword);
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .success(true)
                .data(movies)
                .build();
    }

    //    Them nguoi vao phim
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{movieId}" + ApiPaths.People.BASE + "/{peopleId}")
    public ApiResponse<Void> addPeopleToMovie(@PathVariable String peopleId,
                                              @PathVariable String movieId,
                                              @RequestBody @Valid AddPeopleToMovieRequest request) {
        peopleService.addPeopleToMovie(movieId, peopleId, request);
        log.info("[MOVIE_CONTROLLER] - Add people with id: {} to movie with id: {}", peopleId, movieId);
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }
    //    xaa nguoi khoi phim
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{movieId}" + ApiPaths.People.BASE + "/{peopleId}")
    public ApiResponse<Void> removePeopleFromMovie(@PathVariable String peopleId,
                                                   @PathVariable String movieId) {
        peopleService.removePeopleFromMovie(movieId, peopleId);
        log.info("[MOVIE_CONTROLLER] - Remove people with id: {} from movie with id: {}", peopleId, movieId);
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }

    //    Lay cast cua phim
    @GetMapping("/{movieId}" + ApiPaths.People.BASE)
    public ApiResponse<List<MovieCastResponse>> getMovieCast(@PathVariable String movieId){
        var response = peopleService.getPeopleByMovie(movieId);
        log.info("[MOVIE_CONTROLLER] - Get cast of movie with id: {}, total: {}", movieId, response.size());
        return ApiResponse.<List<MovieCastResponse>>builder()
                .success(true)
                .data(response)
                .build();
    }

}
