package org.example.cinemaBooking.Repository;

import org.example.cinemaBooking.Entity.Cinema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, String> {
    List<Cinema> findCinemaByName(String name);

    Cinema findCinemaById(String id);

    Page<Cinema> findByNameContainingIgnoreCaseAndDeletedFalse(String keyword, Pageable pageable);

    Page<Cinema> findAllByDeletedFalse(Pageable pageable);
}
