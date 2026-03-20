package org.example.cinemaBooking.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Request.ReviewRequest;
import org.example.cinemaBooking.Dto.Response.ReviewResponse;
import org.example.cinemaBooking.Dto.Response.ReviewSummaryResponse;
import org.example.cinemaBooking.Service.ReviewService;
import org.example.cinemaBooking.Shared.constant.ApiPaths;
import org.example.cinemaBooking.Shared.response.ApiResponse;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(ApiPaths.API_V1 + ApiPaths.Review.BASE)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {
    ReviewService reviewService;

    @PostMapping
    ApiResponse<ReviewResponse> createReview(@RequestBody @Valid ReviewRequest reviewRequest) {
        ReviewResponse reviewResponse = reviewService.createReview(reviewRequest);
        log.info("[ReviewController] createReview - reviewId: {}", reviewResponse.id());
        return ApiResponse.<ReviewResponse>builder()
                .success(true)
                .data(reviewResponse)
                .build();
    }

    @PutMapping("/{reviewId}")
    ApiResponse<ReviewResponse> updateReview(@PathVariable String reviewId, @RequestBody @Valid ReviewRequest reviewRequest) {
        ReviewResponse reviewResponse = reviewService.updateReview(reviewId, reviewRequest);
        log.info("[ReviewController] updateReview - reviewId: {}", reviewResponse.id());
        return ApiResponse.<ReviewResponse>builder()
                .success(true)
                .data(reviewResponse)
                .build();
    }

    @DeleteMapping("/{reviewId}")
    ApiResponse<Void> deleteReview(@PathVariable String reviewId) {
        reviewService.deleteReview(reviewId);
        log.info("[ReviewController] deleteReview - reviewId: {}", reviewId);
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }

    @GetMapping("/{reviewId}")
    ApiResponse<ReviewResponse> getReview(@PathVariable String reviewId) {
        ReviewResponse reviewResponse = reviewService.getReview(reviewId);
        log.info("[ReviewController] getReview - reviewId: {}", reviewResponse.id());
        return ApiResponse.<ReviewResponse>builder()
                .success(true)
                .data(reviewResponse)
                .build();
    }

    @GetMapping(ApiPaths.Movie.BASE + "/{movieId}")
    ApiResponse<PageResponse<ReviewSummaryResponse>> getReviewByMovie(
            @PathVariable String movieId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer minimumRating
    ){
        var response = reviewService.getReviewsByMovie(movieId, page, size, minimumRating);
        log.info("[ReviewController] getReviewByMovie - movieId: {}, page: {}, size: {}", movieId, page, size);
        return ApiResponse.<PageResponse<ReviewSummaryResponse>>builder()
                .success(true)
                .data(response)
                .build();
    }

    @GetMapping(ApiPaths.Movie.BASE + "/{movieId}/average-rating")
    ApiResponse<Double> getAverageRatingByMovie(@PathVariable String movieId) {
        Double averageRating = reviewService.getAverageRatingForMovie(movieId);
        log.info("[ReviewController] getAverageRatingByMovie - movieId: {}, averageRating: {}", movieId, averageRating);
        return ApiResponse.<Double>builder()
                .success(true)
                .data(averageRating)
                .build();
    }
}
