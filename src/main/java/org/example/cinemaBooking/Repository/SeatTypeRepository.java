package org.example.cinemaBooking.Repository;

import org.example.cinemaBooking.Entity.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatTypeRepository extends JpaRepository<SeatType, Long> {
}