//package org.example.orderservice.kafka;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.example.orderservice.avro.OrderCreated;
//import org.example.orderservice.avro.OrderStatusChanged;
//import org.example.orderservice.config.KafkaTopics;
//import org.example.orderservice.entity.Order;
//import org.example.orderservice.entity.OrderItem;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//import java.time.Instant;
//import java.util.stream.Collectors;
//
//@Component
//public class OrderEventProducer {
//
//    private final KafkaTemplate<String, OrderCreated> orderCreatedKafkaTemplate;
//    private final KafkaTemplate<String, OrderStatusChanged> orderStatusChangedKafkaTemplate;
//    private final KafkaTopics kafkaTopics;
//
//    public void sendOrderCreated(Order order) {
//        try {
//            OrderCreated event = OrderCreated.newBuilder()
//                    .setOrderId(order.getId())
//                    .setUserId(order.getUserId())
//                    .setItems(order.getItems().stream()
//                            .map(this::toAvroOrderItem)
//                            .collect(Collectors.toList()))
//                    .setTimestamp(Instant.now().toString())
//                    .build();
//
//            orderCreatedKafkaTemplate.send(
//                    kafkaTopics.getOrderCreated(),
//                    order.getId().toString(),
//                    event
//            );
//
//            log.info("Sent {} event for orderId: {}", kafkaTopics.getOrderCreated(), order.getId());
//        } catch (Exception e) {
//            log.error("Failed to send {} event for orderId: {}",
//                    kafkaTopics.getOrderCreated(), order.getId(), e);
//            throw new RuntimeException("Failed to publish " + kafkaTopics.getOrderCreated() + " event", e);
//        }
//    }
//
//    public void sendOrderStatusChanged(Long orderId, Long userId, String oldStatus, String newStatus) {
//        try {
//            OrderStatusChanged event = OrderStatusChanged.newBuilder()
//                    .setOrderId(orderId)
//                    .setUserId(userId)
//                    .setOldStatus(oldStatus)
//                    .setNewStatus(newStatus)
//                    .setTimestamp(Instant.now().toString())
//                    .build();
//
//            orderStatusChangedKafkaTemplate.send(
//                    kafkaTopics.getOrderStatusChanged(),
//                    orderId.toString(),
//                    event
//            );
//
//            log.info("Sent {} event for orderId: {}, {} -> {}",
//                    kafkaTopics.getOrderStatusChanged(), orderId, oldStatus, newStatus);
//        } catch (Exception e) {
//            log.error("Failed to send {} event for orderId: {}",
//                    kafkaTopics.getOrderStatusChanged(), orderId, e);
//            throw new RuntimeException("Failed to publish " + kafkaTopics.getOrderStatusChanged() + " event", e);
//        }
//    }
//
//    private org.example.orderservice.avro.OrderItem toAvroOrderItem(OrderItem item) {
//        return org.example.orderservice.avro.OrderItem.newBuilder()
//                .setProductId(item.getProductId())
//                .setQuantity(item.getQuantity())
//                .setPrice(item.getPrice() != null ? item.getPrice() : 0L)
//                .build();
//    }
//}