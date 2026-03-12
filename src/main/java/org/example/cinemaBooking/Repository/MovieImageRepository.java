package org.example.cinemaBooking.Repository;

import org.example.cinemaBooking.Entity.MovieImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieImageRepository extends JpaRepository<MovieImage, String> {
}
