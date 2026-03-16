package org.example.cinemaBooking.Repository;

import org.example.cinemaBooking.Entity.MoviePeople;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoviePeopleRepository extends JpaRepository<MoviePeople, String> {
    boolean existsByPeopleId(String id);

    List<MoviePeople> findByPeopleId(String peopleId);

    boolean existsByMovieIdAndPeopleId(String moveId, String peopleId);

    List<MoviePeople> findByMovieId(String movieId);
}
