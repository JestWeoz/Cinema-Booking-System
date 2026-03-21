package org.example.cinemaBooking.Repository;


import org.example.cinemaBooking.Entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    Page<Room> findByNameContainingIgnoreCaseAndDeletedFalse(String keyword, Pageable pageable);
    Page<Room> findAllByDeletedFalse(Pageable deleted);
}
