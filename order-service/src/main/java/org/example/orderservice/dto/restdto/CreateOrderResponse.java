package org.example.orderservice.dto.restdto;

import org.example.orderservice.entity.OrderStatus;

import java.time.Instant;

public record CreateOrderResponse(
        Long orderId,
        OrderStatus status,
        Instant createdAt
) {}
