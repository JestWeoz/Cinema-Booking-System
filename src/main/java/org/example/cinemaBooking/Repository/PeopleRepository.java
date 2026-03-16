package org.example.cinemaBooking.Repository;

import org.example.cinemaBooking.Entity.People;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeopleRepository extends JpaRepository<People, String> {
    Page<People> findByNameContainingIgnoreCase(String key, Pageable pageable);
}
