// ─────────────────────────────────────────────────────────────────
// PaymentRepository.java
// ─────────────────────────────────────────────────────────────────
package org.example.cinemaBooking.Repository;

import org.example.cinemaBooking.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    // FIX P7: thêm JOIN FETCH để tránh LazyInitializationException
    @Query("""
        SELECT p FROM Payment p
        JOIN FETCH p.booking b
        WHERE b.id = :bookingId
          AND p.deletedAt IS NULL
        """)
    Optional<Payment> findByBookingId(@Param("bookingId") String bookingId);

    /**
     * Dùng cho refund — cần JOIN FETCH đầy đủ để cancel booking + release ghế
     */
    @Query("""
        SELECT p FROM Payment p
        JOIN FETCH p.booking b
        JOIN FETCH b.showtime st
        JOIN FETCH st.movie
        LEFT JOIN FETCH b.tickets t
        LEFT JOIN FETCH t.seat s
        LEFT JOIN FETCH s.seatType
        WHERE b.id = :bookingId
          AND p.deletedAt IS NULL
        """)
    Optional<Payment> findByBookingIdWithDetails(@Param("bookingId") String bookingId);

    Optional<Payment> findByTransactionId(String transactionId);

    /** IPN handler dùng — tìm theo bookingCode (vnp_TxnRef) */
    @Query("""
        SELECT p FROM Payment p
        JOIN FETCH p.booking b
        JOIN FETCH b.showtime st
        JOIN FETCH st.movie
        LEFT JOIN FETCH b.tickets t
        LEFT JOIN FETCH t.seat
        WHERE b.bookingCode = :bookingCode
          AND p.deletedAt IS NULL
        """)
    Optional<Payment> findByBookingCode(@Param("bookingCode") String bookingCode);
}
