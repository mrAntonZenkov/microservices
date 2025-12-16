package org.example.orderservice.controller;

import org.example.orderservice.dto.restdto.CreateOrderRequest;
import org.example.orderservice.dto.restdto.CreateOrderResponse;
import org.example.orderservice.dto.restdto.OrderResponse;
import org.example.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestBody CreateOrderRequest request,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName()); // JWT должен содержать userId
        return ResponseEntity.ok(orderService.createOrder(request, userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(orderService.getOrderById(orderId, userId));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<OrderResponse> payOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.payOrder(orderId));
    }

    @PostMapping("/{orderId}/ship")
    public ResponseEntity<OrderResponse> shipOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.shipOrder(orderId));
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<OrderResponse> completeOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.completeOrder(orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }
}