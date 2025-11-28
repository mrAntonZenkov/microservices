package org.example.orderservice.dto.kafkadto;

import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        Long userId,
        List<OrderItemEvent> items
) {}
