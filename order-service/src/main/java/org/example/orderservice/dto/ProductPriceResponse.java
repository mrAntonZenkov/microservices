package org.example.orderservice.dto;

public record ProductPriceResponse(
        Long productId,
        Long price
) {}
