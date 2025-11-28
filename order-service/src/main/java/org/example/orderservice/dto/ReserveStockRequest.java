package org.example.orderservice.dto;

public record ReserveStockRequest(
        Long productId,
        Integer quantity
) {}
