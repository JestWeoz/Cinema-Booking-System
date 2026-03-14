package org.example.cinemaBooking.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.cinemaBooking.Shared.persistence.SoftDeletableEntity;
import org.example.cinemaBooking.Shared.utils.DiscountType;
import org.example.cinemaBooking.Shared.utils.Status;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Promotion extends SoftDeletableEntity {
    @Column(nullable = false, unique = true, length = 50)
    String code;
    @Column(nullable = false, length = 50)
    String name;

    @Column(columnDefinition = "TEXT")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    DiscountType discountType;

    @Column(nullable = false, precision = 10, scale = 2)
    BigDecimal discountValue;

    @Builder.Default
    @Column(precision = 10, scale = 2)
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal maxDiscount;  // giới hạn khi dùng PERCENTAGE

    @Column(nullable = false)
    private Integer quantity = 0;   // tổng phát hành

    @Column(nullable = false)
    private Integer usedQuantity = 0; // đã dùng

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Status status = Status.ACTIVE;

    @Column(nullable = false)
    LocalDate startDate;

    @Column(nullable = false)
    LocalDate endDate;
}
