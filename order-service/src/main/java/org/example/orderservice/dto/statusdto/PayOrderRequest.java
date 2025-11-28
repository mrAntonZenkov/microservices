package org.example.orderservice.dto.statusdto;

public record PayOrderRequest(
        String paymentToken
) {}