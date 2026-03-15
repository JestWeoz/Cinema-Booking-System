package org.example.cinemaBooking.Repository;

import org.example.cinemaBooking.Entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String>,
        JpaSpecificationExecutor<Movie> {

    @Override
    @EntityGraph(attributePaths = {"categories"})
    Page<Movie> findAll(Specification<Movie> spec, Pageable pageable);
}
