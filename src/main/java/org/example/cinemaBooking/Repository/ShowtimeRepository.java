package org.example.cinemaBooking.Repository;

import org.example.cinemaBooking.Entity.Showtime;
import org.example.cinemaBooking.Shared.utils.ShowTimeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShowtimeRepository
        extends JpaRepository<Showtime, String>, JpaSpecificationExecutor<Showtime> {

    // ── Fetch với JOIN để tránh N+1 ──────────────────────────────────

    @Query("""
            SELECT s FROM Showtime s
              JOIN FETCH s.movie m
              JOIN FETCH s.room  r
              JOIN FETCH r.cinema c
            WHERE s.id = :id
              AND s.deletedAt IS NULL
            """)
    Optional<Showtime> findByIdWithDetails(@Param("id") String id);

    // ── Lịch chiếu theo phim + ngày ──────────────────────────────────

    @Query("""
            SELECT s FROM Showtime s
              JOIN FETCH s.movie m
              JOIN FETCH s.room  r
              JOIN FETCH r.cinema c
            WHERE m.id         = :movieId
              AND s.startTime >= :from
              AND s.startTime <  :to
              AND s.status    != :excluded
              AND s.deletedAt IS NULL
            ORDER BY s.startTime ASC
            """)
    List<Showtime> findByMovieAndDateRange(
            @Param("movieId")  String movieId,
            @Param("from")     LocalDateTime from,
            @Param("to")       LocalDateTime to,
            @Param("excluded") ShowTimeStatus excluded
    );

    // ── Lịch chiếu theo rạp + ngày ───────────────────────────────────

    @Query("""
            SELECT s FROM Showtime s
              JOIN FETCH s.movie m
              JOIN FETCH s.room  r
              JOIN FETCH r.cinema c
            WHERE c.id         = :cinemaId
              AND s.startTime >= :from
              AND s.startTime <  :to
              AND s.status    != :excluded
              AND s.deletedAt IS NULL
            ORDER BY s.startTime ASC
            """)
    List<Showtime> findByCinemaAndDateRange(
            @Param("cinemaId") String cinemaId,
            @Param("from")     LocalDateTime from,
            @Param("to")       LocalDateTime to,
            @Param("excluded") ShowTimeStatus excluded
    );

    // ── Kiểm tra conflict lịch chiếu cùng phòng ──────────────────────
    // Dùng khi tạo / chỉnh suất: đảm bảo không overlap với buffer 20 phút

    @Query("""
            SELECT COUNT(s) > 0 FROM Showtime s
              JOIN s.movie m
            WHERE s.room.id   = :roomId
              AND s.id        != :excludeId
              AND s.status   NOT IN ('CANCELLED')
              AND s.deletedAt IS NULL
              AND :newStart   < (s.startTime + (m.duration + 20) * 60 second)
              AND :newEnd     > s.startTime
            """)
    boolean existsConflict(
            @Param("roomId")    String roomId,
            @Param("excludeId") String excludeId,   // 0L khi tạo mới
            @Param("newStart")  LocalDateTime newStart,
            @Param("newEnd")    LocalDateTime newEnd
    );

    // ── Các suất cần tự động chuyển trạng thái (dùng bởi scheduler) ──

    @Query("""
            SELECT s FROM Showtime s
              JOIN FETCH s.movie m
            WHERE s.status    = 'SCHEDULED'
              AND s.startTime <= :now
              AND s.deletedAt IS NULL
            """)
    List<Showtime> findScheduledShowtimesToStart(@Param("now") LocalDateTime now);

    @Query("""
            SELECT s FROM Showtime s
              JOIN FETCH s.movie m
            WHERE s.status    = 'ONGOING'
              AND s.deletedAt IS NULL
            """)
    List<Showtime> findAllOngoing();

    // ── Page với dynamic filter (qua Specification) ───────────────────
    // Kế thừa từ JpaSpecificationExecutor: findAll(spec, pageable)
}