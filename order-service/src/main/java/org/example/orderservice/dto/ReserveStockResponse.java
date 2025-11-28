package org.example.orderservice.dto;

public record ReserveStockResponse(
        boolean success,
        Integer available
) {}
