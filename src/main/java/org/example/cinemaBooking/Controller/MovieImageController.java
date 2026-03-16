package org.example.cinemaBooking.Controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Request.CreateMovieImageRequest;
import org.example.cinemaBooking.Dto.Response.MovieImageResponse;
import org.example.cinemaBooking.Service.MovieImageService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(ApiPaths.API_V1 + ApiPaths.Movie.BASE)
public class MovieImageController {
    MovieImageService movieImageService;

    @PostMapping( ApiPaths.Movie.IMAGE)
    ApiResponse<List<MovieImageResponse>> createMovieImage(@RequestBody @Valid CreateMovieImageRequest request) {
        List<MovieImageResponse> movieResponseList = movieImageService.createMovieImage(request);
        log.info("[MOVIE_IMAGE_CONTROLLER] Movie image created successfully");
        return ApiResponse.<List<MovieImageResponse>>builder()
                .success(true)
                .message("Movie image created successfully")
                .data(movieResponseList)
                .build();
    }
    @PutMapping("/{movieId}" + ApiPaths.Movie.IMAGE)
     ApiResponse<Void> updateMovieImage(@PathVariable String movieId, @RequestBody List<String> imageUrls) {
        movieImageService.updateMovieImage(movieId, imageUrls);
        log.info("[MOVIE_IMAGE_CONTROLLER] Movie image updated successfully");
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Movie image updated successfully")
                .data(null)
                .build();
     }

     @DeleteMapping("/{movieId}" + ApiPaths.Movie.IMAGE+ "/{imageId}")
     ApiResponse<Void> deleteMovieImage(@PathVariable String movieId,
                                        @PathVariable String imageId) {
        movieImageService.deleteMovieImage(movieId, imageId);
        log.info("[MOVIE_IMAGE_CONTROLLER] Movie image deleted successfully");
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Movie image deleted successfully")
                .build();
     }

     @GetMapping("/{movieId}" + ApiPaths.Movie.IMAGE)
        ApiResponse<List<MovieImageResponse>> getMovieImagesByMovieId(@PathVariable String movieId) {
            List<MovieImageResponse> movieImageResponses = movieImageService.getMovieImageByMovieId(movieId);
            log.info("[MOVIE_IMAGE_CONTROLLER] Movie images retrieved successfully for movieId: {}", movieId);
            return ApiResponse.<List<MovieImageResponse>>builder()
                    .success(true)
                    .message("Movie images retrieved successfully")
                    .data(movieImageResponses)
                    .build();
        }
}
