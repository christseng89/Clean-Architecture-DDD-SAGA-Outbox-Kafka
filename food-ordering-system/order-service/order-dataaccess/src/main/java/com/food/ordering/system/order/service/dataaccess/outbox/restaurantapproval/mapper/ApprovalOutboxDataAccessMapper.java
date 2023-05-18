package com.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.mapper;

import com.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.entity.ApprovalOutboxEntity;
import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantOutboxMessage;
import org.springframework.stereotype.Component;

@Component
public class ApprovalOutboxDataAccessMapper {

  public ApprovalOutboxEntity orderCreatedOutboxMessageToOutboxEntity(
    OrderRestaurantOutboxMessage orderRestaurantOutboxMessage) {
    return ApprovalOutboxEntity.builder()
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

  public OrderRestaurantOutboxMessage approvalOutboxEntityToOrderApprovalOutboxMessage(
    ApprovalOutboxEntity approvalOutboxEntity) {
    return OrderRestaurantOutboxMessage.builder()
      .id(approvalOutboxEntity.getId())
      .sagaId(approvalOutboxEntity.getSagaId())
      .createdAt(approvalOutboxEntity.getCreatedAt())
      .type(approvalOutboxEntity.getType())
      .payload(approvalOutboxEntity.getPayload())
      .orderStatus(approvalOutboxEntity.getOrderStatus())
      .sagaStatus(approvalOutboxEntity.getSagaStatus())
      .outboxStatus(approvalOutboxEntity.getOutboxStatus())
      .version(approvalOutboxEntity.getVersion())
      .build();
  }

}
