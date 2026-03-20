package org.example.cinemaBooking.Dto.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ComboItemRequest(
        @NotBlank(message = "PRODUCT_ID_REQUIRED") String productId,
        @Min(value = 1, message = "QUANTITY_MIN_VALUE") Integer quantity
) {}