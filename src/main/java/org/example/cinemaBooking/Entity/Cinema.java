package org.example.cinemaBooking.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.cinemaBooking.Shared.persistence.SoftDeletableEntity;
import org.example.cinemaBooking.Shared.utils.Status;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Cinema extends SoftDeletableEntity {
    @Column(nullable = false)
    String name;
    @Column(length = 50)
    String address;

    @Column(length = 50)
    String phone;

    @Column(length = 50)
    String hotline;

    @Column(length = 50)
    String logoUrl;

    @Enumerated(EnumType.STRING)
    Status status;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Room> rooms;
}
