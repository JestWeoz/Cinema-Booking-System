package org.example.cinemaBooking.Dto.Response;

import java.math.BigDecimal;
import java.util.List;

public record ComboResponse(
        String id,
        String name,
        BigDecimal price,
        String image,
        String description,
        boolean active,
        List<ComboItemResponse> items
) {}