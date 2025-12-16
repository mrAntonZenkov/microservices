package org.example.orderservice.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopics {

    private final String orderCreated;
    private final String orderStatusChanged;
    private final String inventoryReserved;

    public KafkaTopics(
            @Value("${spring.kafka.topics.order-created}") String orderCreated,
            @Value("${spring.kafka.topics.order-status-changed}") String orderStatusChanged,
            @Value("${spring.kafka.topics.inventory-reserved}") String inventoryReserved) {
        this.orderCreated = orderCreated;
        this.orderStatusChanged = orderStatusChanged;
        this.inventoryReserved = inventoryReserved;
    }

    public String getOrderCreated() {
        return orderCreated;
    }

    public String getOrderStatusChanged() {
        return orderStatusChanged;
    }

    public String getInventoryReserved() {
        return inventoryReserved;
    }
}