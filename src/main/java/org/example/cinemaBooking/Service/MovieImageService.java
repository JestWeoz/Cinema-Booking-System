package org.example.cinemaBooking.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Entity.Movie;
import org.example.cinemaBooking.Entity.MovieImage;
import org.example.cinemaBooking.Exception.AppException;
import org.example.cinemaBooking.Exception.ErrorCode;
import org.example.cinemaBooking.Mapper.MovieImageMapper;
import org.example.cinemaBooking.Model.Request.CreateMovieImageRequest;
import org.example.cinemaBooking.Model.Response.MovieImageResponse;
import org.example.cinemaBooking.Repository.MovieImageRepository;
import org.example.cinemaBooking.Repository.MovieRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class MovieImageService {
    MovieRepository movieRepository;
    MovieImageRepository movieImageRepository;
    MovieImageMapper movieImageMapper;

    public List<MovieImageResponse> createMovieImage(CreateMovieImageRequest request){
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        List<MovieImage> movieImage = request.getImageUrls().stream()
                .map(imageUrl
                        -> MovieImage.builder()
                        .imageUrl(imageUrl)
                        .movie(movie)
                        .build()).toList();

        movieImageRepository.saveAll(movieImage);
        log.info("[MOVIE_IMAGE_SERVICE]Movie image save successful");
        return movieImage.stream()
                .map(movieImageMapper::toResponse)
                .toList();
    }

    @Transactional
    public void updateMovieImage(String movieId, List<String> imageUrls) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        movieImageRepository.deleteByMovieId(movieId);

        List<MovieImage> newMovieImages = imageUrls.stream()
                .map(imageUrl
                        -> MovieImage.builder()
                        .imageUrl(imageUrl)
                        .movie(movie)
                        .build()).toList();

        movieImageRepository.saveAll(newMovieImages);
        log.info("[MOVIE_IMAGE_SERVICE]Movie image update successful");
    }


    public void deleteMovieImage(String movieId, String imageId) {
        MovieImage movieImage = movieImageRepository.findByIdAndMovieId(imageId, movieId)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_IMAGE_NOT_FOUND));
        movieImageRepository.delete(movieImage);
        log.info("[MOVIE_IMAGE_SERVICE]Movie image delete successful");
    }

    public  List<MovieImageResponse> getMovieImageByMovieId(String movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        List<MovieImage> movieImages = movieImageRepository.findByMovieId(movieId);
        return movieImages.stream()
                .map(movieImageMapper::toResponse)
                .toList();
    }

}
