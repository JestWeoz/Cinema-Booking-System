package org.example.cinemaBooking.Repository;

import org.example.cinemaBooking.Entity.Combo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComboRepository extends JpaRepository<Combo, String> {
}
