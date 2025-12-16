package org.example.orderservice.mapper;

import org.example.orderservice.dto.OrderItemDto;
import org.example.orderservice.dto.restdto.CreateOrderRequest;
import org.example.orderservice.dto.restdto.CreateOrderResponse;
import org.example.orderservice.dto.restdto.OrderResponse;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "NEW")
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "comment", source = "comment")
    Order toEntity(CreateOrderRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "items", source = "items")
    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "message", constant = "Order created successfully")
    CreateOrderResponse toCreateOrderResponse(Order order);

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "quantity", source = "quantity")
    OrderItemDto toOrderItemDto(OrderItem orderItem);

    List<OrderItemDto> toOrderItemDtoList(List<OrderItem> items);
}
