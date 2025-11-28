package org.example.orderservice.dto.restdto;

import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull Long productId,
        @NotNull Integer quantity
) {}
