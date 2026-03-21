package org.example.cinemaBooking.Dto.Response;

public record ComboItemResponse(
        String productId,
        String productName,
        Integer quantity
) {}