package com.food.ordering.system.restaurant.service.dataaccess.restaurant.outbox.mapper;

import com.food.ordering.system.restaurant.service.dataaccess.restaurant.outbox.entity.OrderOutboxEntity;
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import org.springframework.stereotype.Component;

@Component
public class OrderOutboxDataAccessMapper {

  public OrderOutboxEntity orderOutboxMessageToOutboxEntity(OrderOutboxMessage orderOutboxMessage) {
    return OrderOutboxEntity.builder()
      .id(orderOutboxMessage.getId())
      .sagaId(orderOutboxMessage.getSagaId())
      .createdAt(orderOutboxMessage.getCreatedAt())
      .type(orderOutboxMessage.getType())
      .payload(orderOutboxMessage.getPayload())
      .outboxStatus(orderOutboxMessage.getOutboxStatus())
      .approvedStatus(orderOutboxMessage.getApprovedStatus())
      .version(orderOutboxMessage.getVersion())
      .build();
  }

  public OrderOutboxMessage orderOutboxEntityToOrderOutboxMessage(OrderOutboxEntity paymentOutboxEntity) {
    return OrderOutboxMessage.builder()
      .id(paymentOutboxEntity.getId())
      .sagaId(paymentOutboxEntity.getSagaId())
      .createdAt(paymentOutboxEntity.getCreatedAt())
      .type(paymentOutboxEntity.getType())
      .payload(paymentOutboxEntity.getPayload())
      .outboxStatus(paymentOutboxEntity.getOutboxStatus())
      .approvedStatus(paymentOutboxEntity.getApprovedStatus())
      .version(paymentOutboxEntity.getVersion())
      .build();
  }

}
