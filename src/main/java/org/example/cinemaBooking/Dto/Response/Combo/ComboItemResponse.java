package org.example.cinemaBooking.Dto.Response.Combo;

public record ComboItemResponse(
        String productId,
        String productName,
        Integer quantity
) {}