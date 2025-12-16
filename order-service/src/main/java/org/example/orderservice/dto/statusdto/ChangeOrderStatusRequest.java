package org.example.orderservice.dto.statusdto;

public record ChangeOrderStatusRequest(
        OrderStatusDto newStatus
) {}
