package org.example.cinemaBooking.Repository;

import org.example.cinemaBooking.Entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
    @Query("""
        SELECT t FROM Ticket t
        JOIN FETCH t.seat s
        JOIN FETCH s.seatType
        WHERE t.booking.id = :bookingId AND t.deletedAt IS NULL
        """)
    List<Ticket> findAllByBookingId(@Param("bookingId") String bookingId);

    Optional<Ticket> findByTicketCode(String ticketCode);
}
