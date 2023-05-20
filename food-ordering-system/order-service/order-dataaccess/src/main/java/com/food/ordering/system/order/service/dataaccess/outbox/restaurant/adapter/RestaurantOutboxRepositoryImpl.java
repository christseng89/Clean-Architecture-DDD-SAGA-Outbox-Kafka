package com.food.ordering.system.order.service.dataaccess.outbox.restaurant.adapter;

import com.food.ordering.system.order.service.dataaccess.outbox.restaurant.exception.RestaurantOutboxNotFoundException;
import com.food.ordering.system.order.service.dataaccess.outbox.restaurant.mapper.RestaurantOutboxDataAccessMapper;
import com.food.ordering.system.order.service.dataaccess.outbox.restaurant.repository.RestaurantOutboxJpaRepository;
import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantOutboxRepository;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RestaurantOutboxRepositoryImpl implements RestaurantOutboxRepository {

  private final RestaurantOutboxJpaRepository restaurantOutboxJpaRepository;
  private final RestaurantOutboxDataAccessMapper restaurantOutboxDataAccessMapper;

  public RestaurantOutboxRepositoryImpl(
    RestaurantOutboxJpaRepository restaurantOutboxJpaRepository,
    RestaurantOutboxDataAccessMapper restaurantOutboxDataAccessMapper) {
    this.restaurantOutboxJpaRepository = restaurantOutboxJpaRepository;
    this.restaurantOutboxDataAccessMapper = restaurantOutboxDataAccessMapper;
  }

  @Override
  public OrderRestaurantOutboxMessage save(OrderRestaurantOutboxMessage orderRestaurantOutboxMessage) {
    return restaurantOutboxDataAccessMapper
      .approvalOutboxEntityToOrderApprovalOutboxMessage(restaurantOutboxJpaRepository
        .save(restaurantOutboxDataAccessMapper
          .orderCreatedOutboxMessageToOutboxEntity(orderRestaurantOutboxMessage)));
  }

  @Override
  public Optional<List<OrderRestaurantOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(
    String sagaType, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
    return Optional.of(restaurantOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(
        sagaType, outboxStatus, Arrays.asList(sagaStatus))
      .orElseThrow(() -> new RestaurantOutboxNotFoundException("Approval outbox object " +
        "could be found for saga type " + sagaType))
      .stream()
      .map(restaurantOutboxDataAccessMapper::approvalOutboxEntityToOrderApprovalOutboxMessage)
      .toList());
  }

  @Override
  public Optional<OrderRestaurantOutboxMessage> findByTypeAndSagaIdAndSagaStatus(
    String type, UUID sagaId, SagaStatus... sagaStatus) {
    return restaurantOutboxJpaRepository
      .findByTypeAndSagaIdAndSagaStatusIn(type, sagaId,
        Arrays.asList(sagaStatus))
      .map(restaurantOutboxDataAccessMapper::approvalOutboxEntityToOrderApprovalOutboxMessage);

  }

  @Override
  public void deleteByTypeAndOutboxStatusAndSagaStatus(
    String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
    restaurantOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(type, outboxStatus,
      Arrays.asList(sagaStatus));
  }
}
