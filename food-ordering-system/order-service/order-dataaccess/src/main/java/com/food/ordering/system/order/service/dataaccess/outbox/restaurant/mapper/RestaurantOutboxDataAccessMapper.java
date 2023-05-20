package com.food.ordering.system.order.service.dataaccess.outbox.restaurant.mapper;

import com.food.ordering.system.order.service.dataaccess.outbox.restaurant.entity.RestaurantOutboxEntity;
import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantOutboxMessage;
import org.springframework.stereotype.Component;

@Component
public class RestaurantOutboxDataAccessMapper {

  public RestaurantOutboxEntity orderCreatedOutboxMessageToOutboxEntity(
    OrderRestaurantOutboxMessage orderRestaurantOutboxMessage) {
    return RestaurantOutboxEntity.builder()
      .id(orderRestaurantOutboxMessage.getId())
      .sagaId(orderRestaurantOutboxMessage.getSagaId())
      .createdAt(orderRestaurantOutboxMessage.getCreatedAt())
      .type(orderRestaurantOutboxMessage.getType())
      .payload(orderRestaurantOutboxMessage.getPayload())
      .orderStatus(orderRestaurantOutboxMessage.getOrderStatus())
      .sagaStatus(orderRestaurantOutboxMessage.getSagaStatus())
      .outboxStatus(orderRestaurantOutboxMessage.getOutboxStatus())
      .version(orderRestaurantOutboxMessage.getVersion())
      .build();
  }

  public OrderRestaurantOutboxMessage approvalOutboxEntityToRestaurantApprovalOutboxMessage(
    RestaurantOutboxEntity restaurantOutboxEntity) {
    return OrderRestaurantOutboxMessage.builder()
      .id(restaurantOutboxEntity.getId())
      .sagaId(restaurantOutboxEntity.getSagaId())
      .createdAt(restaurantOutboxEntity.getCreatedAt())
      .type(restaurantOutboxEntity.getType())
      .payload(restaurantOutboxEntity.getPayload())
      .orderStatus(restaurantOutboxEntity.getOrderStatus())
      .sagaStatus(restaurantOutboxEntity.getSagaStatus())
      .outboxStatus(restaurantOutboxEntity.getOutboxStatus())
      .version(restaurantOutboxEntity.getVersion())
      .build();
  }

}
