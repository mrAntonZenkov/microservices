package org.example.orderservice.dto.restdto;

public record OrderItemResponse(
        Long productId,
        Integer quantity,
        Long price
) {}