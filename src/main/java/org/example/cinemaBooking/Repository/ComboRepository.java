package org.example.cinemaBooking.Repository;

import org.example.cinemaBooking.Entity.Combo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComboRepository extends JpaRepository<Combo, String> {
    Optional<Combo> findByName(String name);

    @EntityGraph(attributePaths = {"items", "items.product"})
    Page<Combo> findByDeletedFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"items", "items.product"})
    Page<Combo> findByNameContainingIgnoreCaseAndDeletedFalse(String name, Pageable pageable);


    Page<Combo> findByActiveTrueAndDeletedFalse(Pageable pageable);
}
