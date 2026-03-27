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
}
