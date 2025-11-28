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
    @Mapping(target = "status", expression = "java(com.example.order.model.OrderStatus.NEW)")
    Order toEntity(CreateOrderRequest request);

    OrderResponse toOrderResponse(Order order);

    CreateOrderResponse toCreateOrderResponse(Order order);

    List<OrderItemDto> mapItems(List<OrderItem> items);
}
