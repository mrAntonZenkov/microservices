package org.example.orderservice.controller;

import org.example.orderservice.dto.restdto.CreateOrderRequest;
import org.example.orderservice.dto.restdto.CreateOrderResponse;
import org.example.orderservice.dto.restdto.OrderResponse;
import org.example.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Создание заказа
    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestBody CreateOrderRequest request,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName()); // JWT должен содержать userId
        return ResponseEntity.ok(orderService.createOrder(request, userId));
    }

    // Получение заказа по ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(orderService.getOrderById(orderId, userId));
    }

    // Получение всех заказов пользователя
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    // Оплата заказа
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<OrderResponse> payOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.payOrder(orderId));
    }

    // Отправка заказа
    @PostMapping("/{orderId}/ship")
    public ResponseEntity<OrderResponse> shipOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.shipOrder(orderId));
    }

    // Завершение заказа
    @PostMapping("/{orderId}/complete")
    public ResponseEntity<OrderResponse> completeOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.completeOrder(orderId));
    }

    // Отмена заказа
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }
}