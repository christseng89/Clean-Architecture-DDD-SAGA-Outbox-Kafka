package com.food.ordering.system.order.service.domain.ports.output.repository;

import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantOutboxMessage;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApprovalOutboxRepository {

  OrderRestaurantOutboxMessage save(OrderRestaurantOutboxMessage orderRestaurantOutboxMessage);

  Optional<List<OrderRestaurantOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(
    String type,
    OutboxStatus outboxStatus,
    SagaStatus... sagaStatus);

  Optional<OrderRestaurantOutboxMessage> findByTypeAndSagaIdAndSagaStatus(
    String type,
    UUID sagaId,
    SagaStatus... sagaStatus);

  void deleteByTypeAndOutboxStatusAndSagaStatus(
    String type,
    OutboxStatus outboxStatus,
    SagaStatus... sagaStatus);
}
