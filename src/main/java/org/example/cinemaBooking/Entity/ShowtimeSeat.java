package org.example.cinemaBooking.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.cinemaBooking.Shared.persistence.SoftDeletableEntity;
import org.example.cinemaBooking.Shared.utils.SeatStatus;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "showtime_seat",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_showtime_seat",
                columnNames = {"showtime_id", "seat_id"}
        )
)
@Setter
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowtimeSeat extends SoftDeletableEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    SeatStatus status = SeatStatus.AVAILABLE;   // trạng thái ghế: AVAILABLE, BOOKED, LOCKED

    private LocalDateTime lockedUntil;   // hết hạn giữ chỗ tạm

    private Long lockedByUser;           // FK user đang giữ (không hard FK để tránh deadlock)

    // ── Optimistic Locking ────────────────────────────────────────────
    // Dùng để ngăn 2 user cùng LOCK 1 ghế đồng thời
    @Version
    private Long version;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;
}
