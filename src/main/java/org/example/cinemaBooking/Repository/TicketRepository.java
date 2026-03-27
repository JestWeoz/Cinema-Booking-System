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


    @Query("""
        SELECT t FROM Ticket t
        JOIN FETCH t.seat s
        JOIN FETCH s.seatType
        JOIN FETCH t.booking b
        JOIN FETCH b.showtime st
        JOIN FETCH st.movie
        JOIN FETCH st.room r
        JOIN FETCH r.cinema
        WHERE t.ticketCode = :ticketCode
          AND t.deletedAt IS NULL
        """)
    Optional<Ticket> findByTicketCodeWithDetails(@Param("ticketCode") String ticketCode);

    /** Scheduled job — expire vé chưa dùng sau khi suất chiếu kết thúc */
    @Query("""
        SELECT t FROM Ticket t
        JOIN FETCH t.booking b
        JOIN FETCH b.showtime st
        WHERE t.status     = org.example.cinemaBooking.Shared.enums.TicketStatus.VALID
          AND st.status    = org.example.cinemaBooking.Shared.enums.ShowTimeStatus.FINISHED
          AND t.deletedAt IS NULL
        """)
    List<Ticket> findValidTicketsOfFinishedShowtimes();

    /** Lấy tất cả vé của 1 user */
    @Query("""
        SELECT t FROM Ticket t
        JOIN FETCH t.seat s
        JOIN FETCH s.seatType
        JOIN FETCH t.booking b
        JOIN FETCH b.showtime st
        JOIN FETCH st.movie
        JOIN FETCH st.room r
        JOIN FETCH r.cinema
        WHERE b.user.id   = :userId
          AND t.deletedAt IS NULL
        ORDER BY st.startTime DESC
        """)
    List<Ticket> findAllByUserId(@Param("userId") String userId);

    @Query("""
    SELECT t FROM Ticket t
    JOIN FETCH t.seat s
    JOIN FETCH s.seatType
    JOIN FETCH t.booking b
    JOIN FETCH b.showtime st
    JOIN FETCH st.movie
    JOIN FETCH st.room r
    WHERE b.bookingCode = :bookingCode
      AND t.deletedAt IS NULL
    """)
    List<Ticket> findAllByBookingCode(@Param("bookingCode") String bookingCode);
}
