//package org.example.orderservice.mapper;
//
//import org.example.orderservice.dto.kafkadto.OrderCreatedEvent;
//import org.example.orderservice.entity.Order;
//import org.example.orderservice.entity.OrderItem;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.Named;
//
//import java.util.List;
//
//@Mapper(componentModel = "spring", )
//public interface OrderEventMapper {
//
//    @Mapping(target = "orderId", source = "id")
//    @Mapping(target = "userId", source = "userId")
//    @Mapping(target = "status", constant = "NEW")
//    @Mapping(target = "items", source = "items", qualifiedByName = "mapItems")
//    @Mapping(target = "timestamp", expression = "java(java.time.Instant.now().toString())")
//    OrderCreatedEvent toOrderCreatedEvent(Order order);
//
//    @Mapping(target = "orderId", source = "id")
//    @Mapping(target = "userId", source = "userId")
//    @Mapping(target = "oldStatus", source = "oldStatus")
//    @Mapping(target = "newStatus", source = "newStatus")
//    @Mapping(target = "timestamp", expression = "java(java.time.Instant.now().toString())")
//    OrderStatusChangedEvent toOrderStatusChangedEvent(Order order, String oldStatus, String newStatus);
//
//    @Named("mapItems")
//    default List<AvroOrderItem> mapItems(List<OrderItem> items) {
//        return items.stream()
//                .map(this::toAvroOrderItem)
//                .toList();
//    }
//
//    @Mapping(target = "productId", source = "productId")
//    @Mapping(target = "quantity", source = "quantity")
//    AvroOrderItem toAvroOrderItem(OrderItem orderItem);
//}
