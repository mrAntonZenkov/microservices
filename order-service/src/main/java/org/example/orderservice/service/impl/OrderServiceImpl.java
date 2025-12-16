package org.example.orderservice.service.impl;

import org.example.orderservice.dto.restdto.CreateOrderRequest;
import org.example.orderservice.dto.restdto.CreateOrderResponse;
import org.example.orderservice.dto.restdto.OrderResponse;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderItem;
import org.example.orderservice.entity.OrderStatus;
import org.example.orderservice.exception.NotFoundException;
import org.example.orderservice.grpc.InventoryGrpcClient;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.service.OrderService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper mapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final InventoryGrpcClient inventoryClient;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper mapper, KafkaTemplate<String, Object> kafkaTemplate, InventoryGrpcClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
        this.inventoryClient = inventoryClient;
    }

    private final String ORDER_CREATED_TOPIC = "order.created";
    private final String ORDER_STATUS_CHANGED_TOPIC = "order.status-changed";

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, Long userId) {
        Order order = mapper.toEntity(request);
        order.setUserId(userId);

        List<OrderItem> items = request.items().stream().map(i -> {
            OrderItem item = new OrderItem();
            item.setProductId(i.productId());
            item.setQuantity(i.quantity());
            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());

        order.setItems(items);

        order.setStatus(OrderStatus.NEW);
        Order saved = orderRepository.save(order);

        kafkaTemplate.send(ORDER_CREATED_TOPIC, saved.getId().toString(), saved);

        boolean reserved;
        try {
            reserved = inventoryClient.reserveItems(saved.getId(), items);
        } catch (Exception e) {
            saved.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(saved);
            kafkaTemplate.send(ORDER_STATUS_CHANGED_TOPIC, saved.getId().toString(), saved);
            throw new RuntimeException("Inventory reservation failed, order cancelled", e);
        }

        if (!reserved) {
            saved.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(saved);
            kafkaTemplate.send(ORDER_STATUS_CHANGED_TOPIC, saved.getId().toString(), saved);
            throw new RuntimeException("Not enough inventory, order cancelled");
        }

        saved.setStatus(OrderStatus.RESERVED);
        Order reservedOrder = orderRepository.save(saved);
        kafkaTemplate.send(ORDER_STATUS_CHANGED_TOPIC, reservedOrder.getId().toString(), reservedOrder);

        return mapper.toCreateOrderResponse(reservedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new NotFoundException("Order not found: " + orderId);
        }

        return mapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId).stream()
                .map(mapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.RESERVED) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            kafkaTemplate.send(ORDER_STATUS_CHANGED_TOPIC, order.getId().toString(), order);
            throw new RuntimeException("Order not in RESERVED state, payment failed, order cancelled");
        }

        order.setStatus(OrderStatus.PAID);
        Order updated = orderRepository.save(order);
        kafkaTemplate.send(ORDER_STATUS_CHANGED_TOPIC, updated.getId().toString(), updated);
        return mapper.toOrderResponse(updated);
    }

    @Override
    @Transactional
    public OrderResponse shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.PAID) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            kafkaTemplate.send(ORDER_STATUS_CHANGED_TOPIC, order.getId().toString(), order);
            throw new RuntimeException("Order not PAID, cannot ship, order cancelled");
        }

        order.setStatus(OrderStatus.SHIPPED);
        Order updated = orderRepository.save(order);
        kafkaTemplate.send(ORDER_STATUS_CHANGED_TOPIC, updated.getId().toString(), updated);
        return mapper.toOrderResponse(updated);
    }

    @Override
    @Transactional
    public OrderResponse completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.SHIPPED) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            kafkaTemplate.send(ORDER_STATUS_CHANGED_TOPIC, order.getId().toString(), order);
            throw new RuntimeException("Order not SHIPPED, cannot complete, order cancelled");
        }

        order.setStatus(OrderStatus.COMPLETED);
        Order updated = orderRepository.save(order);
        kafkaTemplate.send(ORDER_STATUS_CHANGED_TOPIC, updated.getId().toString(), updated);
        return mapper.toOrderResponse(updated);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        order.setStatus(OrderStatus.CANCELLED);
        Order updated = orderRepository.save(order);
        kafkaTemplate.send(ORDER_STATUS_CHANGED_TOPIC, updated.getId().toString(), updated);
        return mapper.toOrderResponse(updated);
    }
}
