package org.example.cinemaBooking.Controller.Movie;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Request.Movie.CreateMovieImageRequest;
import org.example.cinemaBooking.Dto.Request.Movie.UpdateMovieImageRequest;
import org.example.cinemaBooking.Dto.Response.Movie.MovieImageResponse;
import org.example.cinemaBooking.Service.Movie.MovieImageService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(ApiPaths.API_V1 + ApiPaths.Movie.BASE)
public class MovieImageController {
    MovieImageService movieImageService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping( "/{movieId}" + ApiPaths.Movie.IMAGE)
    public ApiResponse<List<MovieImageResponse>> createMovieImage(@PathVariable String movieId, @RequestBody @Valid CreateMovieImageRequest request) {
        List<MovieImageResponse> movieResponseList = movieImageService.createMovieImage(movieId, request);
        log.info("[MOVIE_IMAGE_CONTROLLER] Movie image created successfully");
        return ApiResponse.<List<MovieImageResponse>>builder()
                .success(true)
                .message("Movie image created successfully")
                .data(movieResponseList)
                .build();
    }

     @PreAuthorize("hasRole('ADMIN')")
     @PutMapping("/{movieId}" + ApiPaths.Movie.IMAGE)
     public ApiResponse<Void> updateMovieImage(@PathVariable String movieId, @RequestBody @Valid UpdateMovieImageRequest request) {
        movieImageService.updateMovieImage(movieId, request);
        log.info("[MOVIE_IMAGE_CONTROLLER] Movie image updated successfully");
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Movie image updated successfully")
                .build();
     }

     @PreAuthorize("hasRole('ADMIN')")
     @DeleteMapping("/{movieId}" + ApiPaths.Movie.IMAGE+ "/{imageId}")
     public ApiResponse<Void> deleteMovieImage(@PathVariable String movieId,
                                        @PathVariable String imageId) {
        movieImageService.deleteMovieImage(movieId, imageId);
        log.info("[MOVIE_IMAGE_CONTROLLER] Movie image deleted successfully");
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Movie image deleted successfully")
                .build();
     }


     @GetMapping("/{movieId}" + ApiPaths.Movie.IMAGE)
     public ApiResponse<List<MovieImageResponse>> getMovieImagesByMovieId(@PathVariable String movieId) {
        List<MovieImageResponse> movieImageResponses = movieImageService.getMovieImageByMovieId(movieId);
        log.info("[MOVIE_IMAGE_CONTROLLER] Movie images retrieved successfully for movieId: {}", movieId);
        return ApiResponse.<List<MovieImageResponse>>builder()
                .success(true)
                .message("Movie images retrieved successfully")
                .data(movieImageResponses)
                .build();
        }
}
