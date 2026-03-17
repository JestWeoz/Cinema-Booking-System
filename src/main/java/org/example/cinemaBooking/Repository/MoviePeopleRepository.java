package org.example.cinemaBooking.Repository;

import org.example.cinemaBooking.Entity.MoviePeople;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface MoviePeopleRepository extends JpaRepository<MoviePeople, String> {
    boolean existsByPeopleId(String id);

    @Query("""
SELECT mp FROM MoviePeople mp
JOIN FETCH mp.movie m
WHERE mp.people.id = :peopleId
""")
    List<MoviePeople> findByPeopleId(String peopleId);

    boolean existsByMovieIdAndPeopleId(String moveId, String peopleId);

    @Query("""
SELECT mp FROM MoviePeople mp
JOIN FETCH mp.people p
WHERE mp.movie.id = :movieId
""")
    List<MoviePeople> findByMovieId(String movieId);

    Optional<MoviePeople> findByMovieIdAndPeopleId(String movieId, String peopleId);

    void deleteByPeopleId(String peopleId);
}
