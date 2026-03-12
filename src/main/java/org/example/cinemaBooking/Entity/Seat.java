package org.example.cinemaBooking.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.cinemaBooking.Shared.persistence.SoftDeletableEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Seat extends SoftDeletableEntity {
    @Column(nullable = false, length = 2)
    String seatRow; // A, B, C, D, E, F, G, H, I, J

    @Column(nullable = false, length = 2)
    Integer seatNumber; // 1, 2, 3, 4, 5, 6, 7, 8, 9, 10


    @Column(nullable = false)
    boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    Room room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_type_id", nullable = false)
    SeatType seatType;
}
