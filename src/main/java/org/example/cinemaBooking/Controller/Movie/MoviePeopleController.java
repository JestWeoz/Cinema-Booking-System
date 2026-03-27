package org.example.cinemaBooking.Controller.Movie;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.DTO.Request.Movie.AddPeopleToMovieRequest;
import org.example.cinemaBooking.DTO.Request.Movie.UpdateMoviePeopleRequest;
import org.example.cinemaBooking.DTO.Response.Movie.MovieCastResponse;
import org.example.cinemaBooking.DTO.Response.Movie.MoviePeopleResponse;
import org.example.cinemaBooking.Service.Movie.MoviePeopleService;
import org.example.cinemaBooking.Service.Movie.PeopleService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(ApiPaths.API_V1 + ApiPaths.Movie.BASE + "/{movieId}" + ApiPaths.People.BASE)
public class MoviePeopleController {
    PeopleService peopleService;
    MoviePeopleService moviePeopleService;
    //    Them nguoi vao phim
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<List<MoviePeopleResponse>> addPeopleToMovie(@PathVariable String movieId,
                                                                   @RequestBody @Valid AddPeopleToMovieRequest request) {
        var data = moviePeopleService.addPeopleToMovie(movieId, request);
        log.info("[MOVIE_PEOPLE_CONTROLLER]_REST request to add people to movie: {}", movieId);
        return ApiResponse.<List<MoviePeopleResponse>>builder()
                .success(true)
                .data(data)
                .build();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<MoviePeopleResponse>> updateMoviePeople(
            @PathVariable String movieId,
            @RequestBody @Valid UpdateMoviePeopleRequest request
    ) {
        log.info("REST request to update people in movie: {}", movieId);
        var response = moviePeopleService.updateMoviePeople(movieId, request);
        return ApiResponse.<List<MoviePeopleResponse>>builder()
                .success(true)
                .data(response)
                .message("Updated people in movie successfully")
                .build();
    }

    /**
     * Xóa 1 người khỏi phim
     * DELETE /api/v1/movies/{movieId}/people/{peopleId}
     */
    @DeleteMapping("/{peopleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> removePeopleFromMovie(
            @PathVariable String movieId,
            @PathVariable String peopleId
    ) {
        log.info("REST request to remove people {} from movie: {}", peopleId, movieId);
        moviePeopleService.removePeopleFromMovie(movieId, peopleId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Removed people from movie successfully")
                .build();
    }

    /**
     * Xóa nhiều người khỏi phim
     * DELETE /api/v1/movies/{movieId}/people/bulk?peopleIds=id1,id2,id3
     */
    @DeleteMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> removeMultiplePeopleFromMovie(
            @PathVariable String movieId,
            @RequestParam List<String> peopleIds
    ) {
        log.info("REST request to remove {} people from movie: {}", peopleIds.size(), movieId);
        moviePeopleService.removeMultiplePeopleFromMovie(movieId, peopleIds);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Removed " + peopleIds.size() + " people from movie successfully")
                .build();
    }

    //    Lay cast cua phim
    @GetMapping
    public ApiResponse<List<MovieCastResponse>> getMovieCast(@PathVariable String movieId){
        var response = moviePeopleService.getPeopleByMovie(movieId);
        log.info("[MOVIE_CONTROLLER] - Get cast of movie with id: {}, total: {}", movieId, response.size());
        return ApiResponse.<List<MovieCastResponse>>builder()
                .success(true)
                .data(response)
                .build();
    }
}
