package com.food.ordering.system.order.service.domain.outbox.scheduler.restaurant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.repository.ApprovalOutboxRepository;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.system.saga.order.SagaConstants.ORDER_SAGA_NAME;

@Slf4j
@Component
public class RestaurantOutboxHelper {

  private final ApprovalOutboxRepository approvalOutboxRepository;
  private final ObjectMapper objectMapper;

  public RestaurantOutboxHelper(
    ApprovalOutboxRepository approvalOutboxRepository,
    ObjectMapper objectMapper) {
    this.approvalOutboxRepository = approvalOutboxRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional(readOnly = true)
  public Optional<List<OrderRestaurantOutboxMessage>>
  getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
    OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
    return approvalOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(
      ORDER_SAGA_NAME, outboxStatus, sagaStatus);
  }

  @Transactional(readOnly = true)
  public Optional<OrderRestaurantOutboxMessage>
  getApprovalOutboxMessageBySagaIdAndSagaStatus(
    UUID sagaId, SagaStatus... sagaStatus) {
    return approvalOutboxRepository.findByTypeAndSagaIdAndSagaStatus(
      ORDER_SAGA_NAME, sagaId, sagaStatus);
  }

  @Transactional
  public void save(OrderRestaurantOutboxMessage orderRestaurantOutboxMessage) {
    OrderRestaurantOutboxMessage response = approvalOutboxRepository
      .save(orderRestaurantOutboxMessage);
    if (response == null) {
      log.error("Could not save OrderApprovalOutboxMessage with outbox id: {}",
        orderRestaurantOutboxMessage.getId());
      throw new OrderDomainException("Could not save OrderApprovalOutboxMessage with outbox id: " +
        orderRestaurantOutboxMessage.getId());
    }
    log.info("OrderApprovalOutboxMessage saved with outbox id: {}", orderRestaurantOutboxMessage.getId());
  }

  @Transactional
  public void saveApprovalOutboxMessage(
    OrderRestaurantEventPayload orderRestaurantEventPayload,
    OrderStatus orderStatus,
    SagaStatus sagaStatus,
    OutboxStatus outboxStatus,
    UUID sagaId) {
    save(OrderRestaurantOutboxMessage.builder()
      .id(UUID.randomUUID())
      .sagaId(sagaId)
      .createdAt(orderRestaurantEventPayload.getCreatedAt())
      .type(ORDER_SAGA_NAME)
      .payload(createPayload(orderRestaurantEventPayload))
      .orderStatus(orderStatus)
      .sagaStatus(sagaStatus)
      .outboxStatus(outboxStatus)
      .build());
  }

  @Transactional
  public void deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(
    OutboxStatus outboxStatus,
    SagaStatus... sagaStatus) {
    approvalOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(
      ORDER_SAGA_NAME, outboxStatus, sagaStatus);
  }

  private String createPayload(OrderRestaurantEventPayload orderRestaurantEventPayload) {
    try {
      return objectMapper.writeValueAsString(orderRestaurantEventPayload);
    } catch (JsonProcessingException e) {
      log.error("Could not create OrderApprovalEventPayload for order id: {}",
        orderRestaurantEventPayload.getOrderId(), e);
      throw new OrderDomainException("Could not create OrderApprovalEventPayload for order id: " +
        orderRestaurantEventPayload.getOrderId(), e);
    }
  }

}
