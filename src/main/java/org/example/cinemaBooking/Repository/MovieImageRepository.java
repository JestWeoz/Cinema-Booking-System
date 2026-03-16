package org.example.cinemaBooking.Repository;

import org.example.cinemaBooking.Entity.Movie;
import org.example.cinemaBooking.Entity.MovieImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieImageRepository extends JpaRepository<MovieImage, String> {
    Page<MovieImage> findByMovieId(String movieId, Pageable pageable);
}
