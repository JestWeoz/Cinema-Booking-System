package org.example.cinemaBooking.Repository;

import jakarta.validation.constraints.NotBlank;
import org.example.cinemaBooking.Entity.Movie;
import org.example.cinemaBooking.Shared.enums.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String>,
        JpaSpecificationExecutor<Movie> {

    @Override
    @EntityGraph(attributePaths = {"categories"})
    Page<Movie> findAll(Specification<Movie> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"categories"})
    Optional<Movie> findByIdAndDeletedFalse(String id);

    @EntityGraph(attributePaths = {"categories"})
    Optional<Movie> findBySlugAndDeletedFalse(String slug);

    @EntityGraph(attributePaths = {"categories"})
    Page<Movie> findByStatusAndDeletedFalse(MovieStatus movieStatus, Pageable pageable);

    @EntityGraph(attributePaths = {"categories"})
    @Query("""
SELECT DISTINCT m FROM Movie m
WHERE (:key IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :key, '%')))
AND m.status IN ('COMING_SOON', 'NOW_SHOWING') 
AND m.deleted = false
""")
    Page<Movie> searchMovie(String key, Pageable pageable);

    Optional<Movie> findBySlug(@NotBlank(message = "SLUG_REQUIRED") String slug);

    boolean existsBySlug(String slug);


//    @Query("""
//    SELECT new org.example.dto.MovieStats(
//        m.id,
//        m.title,
//        m.posterUrl,
//
//        (SELECT COALESCE(SUM(t.price), 0)
//         FROM Ticket t
//         JOIN t.booking b
//         JOIN b.showtime s
//         WHERE s.movie.id = m.id
//           AND b.status = 'CONFIRMED'
//        ),
//
//        (SELECT COUNT(t)
//         FROM Ticket t
//         JOIN t.booking b
//         JOIN b.showtime s
//         WHERE s.movie.id = m.id
//           AND b.status = 'CONFIRMED'
//        ),
//
//        (SELECT COALESCE(AVG(r.rating), 0)
//         FROM Review r
//         WHERE r.movie.id = m.id
//        )
//    )
//    FROM Movie m
//    WHERE m.status = 'NOW_SHOWING'
//""")
//    List<MovieStats> getMovieStats();

}
