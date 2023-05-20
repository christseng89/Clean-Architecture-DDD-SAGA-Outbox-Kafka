package com.food.ordering.system.restaurant.service.domain.mapper;

import com.food.ordering.system.domain.outbox.OrderStatus;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantRequest;
import com.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.event.RestaurantEvent;
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RestaurantDataMapper {
  public Restaurant restaurant(RestaurantRequest restaurantRequest) {
    return Restaurant.builder()
      .restaurantId(new RestaurantId(UUID.fromString(restaurantRequest.getRestaurantId())))
      .orderDetail(OrderDetail.builder()
        .orderId(new OrderId(UUID.fromString(restaurantRequest.getOrderId())))
        .products(restaurantRequest.getProducts().stream()
          .map(product -> Product.builder()
            .productId(product.getId())
            .quantity(product.getQuantity())
            .build())
          .toList())
        .totalAmount(new Money(restaurantRequest.getPrice()))
        .orderStatus(OrderStatus.valueOf(restaurantRequest.getRestaurantOrderStatus().name()))
        .build())
      .build();
  }

  public OrderEventPayload orderEventPayload(RestaurantEvent restaurantEvent) {
    return OrderEventPayload.builder()
      .orderId(restaurantEvent.getOrderApproval().getOrderId().getValue().toString())
      .restaurantId(restaurantEvent.getRestaurantId().getValue().toString())
      .orderApprovalStatus(restaurantEvent.getOrderApproval().getApprovalStatus().name())
      .createdAt(restaurantEvent.getCreatedAt())
      .failureMessages(restaurantEvent.getFailureMessages())
      .build();
  }
}
