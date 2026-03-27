package org.example.cinemaBooking.Dto.Response.Product;

import java.math.BigDecimal;

public record ProductResponse(
        String id,
        String name,
        BigDecimal price,
        String image,
        Boolean active
) {}