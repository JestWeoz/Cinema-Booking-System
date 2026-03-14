package org.example.cinemaBooking.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.cinemaBooking.Shared.persistence.SoftDeletableEntity;
import org.example.cinemaBooking.Shared.utils.MovieRole;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MoviePeople extends SoftDeletableEntity {
    @Column(length = 100)
    String characterName;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    MovieRole movieRole = MovieRole.ACTOR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "people_id", nullable = false)
    People people;
}
