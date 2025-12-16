package org.example.orderservice.kafka;

import org.example.orderservice.dto.kafkadto.OrderCreatedEvent;
import org.example.orderservice.entity.OrderItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.order-created}")
    private String orderCreatedTopic;

    @Value("${spring.kafka.topics.order-status-changed}")
    private String orderStatusChangedTopic;

    public OrderEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderCreated(Long orderId, Long userId,
                                 List<OrderItem> items) {
        OrderCreatedEvent event = OrderCreatedEvent.newBuilder()
                .setOrderId(orderId)
                .setUserId(userId)
                .setStatus("NEW")
                .setItems(items.stream()
                        .map(item -> org.example.orderservice.event.avro.OrderItem.newBuilder()
                                .setProductId(item.getProductId())
                                .setQuantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .setTimestamp(Instant.now().toString())
                .build();

        // Ключ: orderId как строка (СТРОГО по ТЗ п.7: "Key orderId")
        kafkaTemplate.send(orderCreatedTopic, String.valueOf(orderId), event);
    }

    public void sendOrderStatusChanged(Long orderId, Long userId,
                                       String oldStatus, String newStatus) {
        OrderStatusChangedEvent event = OrderStatusChangedEvent.newBuilder()
                .setOrderId(orderId)
                .setUserId(userId)
                .setOldStatus(oldStatus)
                .setNewStatus(newStatus)
                .setTimestamp(Instant.now().toString())
                .build();

        // Ключ: orderId как строка (СТРОГО по ТЗ п.7: "Key orderId")
        kafkaTemplate.send(orderStatusChangedTopic, String.valueOf(orderId), event);
    }
}
