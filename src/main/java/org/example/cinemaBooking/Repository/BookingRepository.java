package org.example.cinemaBooking.Repository;

import org.example.cinemaBooking.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    @Query("""
        SELECT DISTINCT b FROM Booking b
        JOIN FETCH b.user
        JOIN FETCH b.showtime st
        JOIN FETCH st.movie
        JOIN FETCH st.room r
        JOIN FETCH r.cinema
        WHERE b.id = :id AND b.deletedAt IS NULL
        """)
    Optional<Booking> findByIdWithDetails(@Param("id") String id);

    @Query("""
        SELECT b FROM Booking b
        JOIN FETCH b.showtime st
        JOIN FETCH st.movie
        WHERE b.user.id = :userId AND b.deletedAt IS NULL
        ORDER BY b.createdAt DESC
        """)
    List<Booking> findAllByUserId(@Param("userId") String userId);

    /** Tìm booking PENDING đã hết hạn — scheduled job gọi */
    @Query("""
        SELECT b FROM Booking b
        WHERE b.status = org.example.cinemaBooking.Shared.enums.BookingStatus.PENDING
          AND b.expiredAt < :now
          AND b.deletedAt IS NULL
        """)
    List<Booking> findExpiredPendingBookings(@Param("now") LocalDateTime now);

    /** Kiểm tra bookingCode unique */
    boolean existsByBookingCode(String bookingCode);


    Optional<Booking> findByBookingCode(String bookingCode);
}