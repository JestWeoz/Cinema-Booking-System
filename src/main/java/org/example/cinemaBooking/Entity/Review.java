package org.example.cinemaBooking.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.cinemaBooking.Shared.persistence.SoftDeletableEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review extends SoftDeletableEntity {
    @Column(columnDefinition = "TEXT")
    String comment;

    @Column(nullable = false)
    int rating; // 1-> 10

    @Builder.Default
    boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;
}
