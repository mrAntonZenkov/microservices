package org.example.orderservice.dto.restdto;

import org.example.orderservice.dto.statusdto.OrderStatusDto;

import java.util.List;

public record OrderResponse(
        Long orderId,
        Long userId,
        List<OrderItemResponse> items,
        OrderStatusDto status,
        String comment
) {}