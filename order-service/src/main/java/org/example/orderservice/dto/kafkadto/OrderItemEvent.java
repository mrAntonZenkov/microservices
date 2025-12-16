package org.example.orderservice.dto.kafkadto;

public record OrderItemEvent(
        Long productId,
        Integer quantity,
        Long price
) {}
