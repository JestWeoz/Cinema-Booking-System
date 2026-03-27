package org.example.cinemaBooking.Repository.spefication;

import jakarta.persistence.criteria.*;
import org.example.cinemaBooking.DTO.Request.Showtime.ShowtimeFilterRequest;
import org.example.cinemaBooking.Entity.*;

import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification cho dynamic filter suất chiếu.
 * Dùng: showtimeRepository.findAll(ShowtimeSpecification.of(filter), pageable)
 */
public final class ShowtimeSpecification {

    private ShowtimeSpecification() {}

    public static Specification<Showtime> of(ShowtimeFilterRequest f) {
        return (root, query, cb) -> {

            // Eager join để tránh N+1 khi fetch list
            if (Long.class != query.getResultType()) {
                root.fetch("movie",  JoinType.LEFT);
                Fetch<Showtime, Room> roomFetch = root.fetch("room", JoinType.LEFT);
                roomFetch.fetch("cinema", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            // Chỉ lấy bản ghi chưa soft-delete
            predicates.add(cb.isNull(root.get("deletedAt")));

            if (f.movieId() != null) {
                predicates.add(cb.equal(root.get("movie").get("id"), f.movieId()));
            }

            if (f.roomId() != null) {
                predicates.add(cb.equal(root.get("room").get("id"), f.roomId()));
            }

            if (f.cinemaId() != null) {
                Join<Showtime, Room>   roomJoin   = root.join("room",   JoinType.LEFT);
                Join<Room, Cinema>     cinemaJoin = roomJoin.join("cinema", JoinType.LEFT);
                predicates.add(cb.equal(cinemaJoin.get("id"), f.cinemaId()));
            }

            if (f.date() != null) {
                LocalDateTime from = f.date().atStartOfDay();
                LocalDateTime to   = from.plusDays(1);
                predicates.add(cb.between(root.get("startTime"), from, to));
            }

            if (f.language() != null) {
                predicates.add(cb.equal(root.get("language"), f.language()));
            }

            if (f.status() != null) {
                predicates.add(cb.equal(root.get("status"), f.status()));
            }

            query.orderBy(cb.asc(root.get("startTime")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}