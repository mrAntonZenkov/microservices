package org.example.orderservice.service;

import org.example.orderservice.dto.restdto.CreateOrderRequest;
import org.example.orderservice.dto.restdto.CreateOrderResponse;
import org.example.orderservice.dto.restdto.OrderResponse;
import org.example.orderservice.entity.OrderStatus;

import java.util.List;

public interface OrderService {

    CreateOrderResponse createOrder(CreateOrderRequest request, Long userId);

    OrderResponse getOrderById(Long orderId, Long userId);

    List<OrderResponse> getOrdersByUserId(Long userId);

    OrderResponse payOrder(Long orderId);

    OrderResponse shipOrder(Long orderId);

    OrderResponse completeOrder(Long orderId);

    OrderResponse cancelOrder(Long orderId);
}
