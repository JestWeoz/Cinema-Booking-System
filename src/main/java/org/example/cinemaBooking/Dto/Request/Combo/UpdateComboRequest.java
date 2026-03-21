package org.example.cinemaBooking.Dto.Request;

import java.math.BigDecimal;
import java.util.List;

public record UpdateComboRequest(
        String name,
        BigDecimal price,
        String image,
        String description,
        List<ComboItemRequest> items,
        Boolean active
) {}