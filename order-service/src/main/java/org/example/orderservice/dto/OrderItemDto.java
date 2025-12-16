package org.example.orderservice.dto;

public record OrderItemDto(
        Long productId,
        Integer quantity
) {}
