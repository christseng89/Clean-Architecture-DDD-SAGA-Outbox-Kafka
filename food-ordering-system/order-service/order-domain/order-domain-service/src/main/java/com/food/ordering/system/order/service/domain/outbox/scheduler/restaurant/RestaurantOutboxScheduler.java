package com.food.ordering.system.order.service.domain.outbox.scheduler.restaurant;

import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurant.RestaurantRequestMessagePublisher;
import com.food.ordering.system.outbox.OutboxScheduler;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RestaurantOutboxScheduler implements OutboxScheduler {

  private final RestaurantOutboxHelper restaurantOutboxHelper;
  private final RestaurantRequestMessagePublisher restaurantRequestMessagePublisher;

  public RestaurantOutboxScheduler(
    RestaurantOutboxHelper restaurantOutboxHelper,
    RestaurantRequestMessagePublisher restaurantRequestMessagePublisher) {
    this.restaurantOutboxHelper = restaurantOutboxHelper;
    this.restaurantRequestMessagePublisher = restaurantRequestMessagePublisher;
  }

  @Override
  @Transactional
  @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
    initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
  public void processOutboxMessage() {
    Optional<List<OrderRestaurantOutboxMessage>> outboxMessagesResponse =
      restaurantOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
        OutboxStatus.STARTED,
        SagaStatus.PROCESSING);

    if (outboxMessagesResponse.isPresent() && !outboxMessagesResponse.get().isEmpty()) {
      List<OrderRestaurantOutboxMessage> outboxMessages = outboxMessagesResponse.get();
      log.info("Received {} RestaurantApprovalOutboxMessage with ids: {}, sending to message bus!",
        outboxMessages.size(),
        outboxMessages.stream()
          .map(outboxMessage -> outboxMessage.getId().toString()).collect(Collectors.joining(",")));

      outboxMessages.forEach(outboxMessage ->
        restaurantRequestMessagePublisher.publish(outboxMessage, this::updateOutboxStatus));
      log.info("{} RestaurantApprovalOutboxMessage sent to message bus!", outboxMessages.size());

    }
  }

  private void updateOutboxStatus(OrderRestaurantOutboxMessage orderRestaurantOutboxMessage, OutboxStatus outboxStatus) {
    orderRestaurantOutboxMessage.setOutboxStatus(outboxStatus);
    restaurantOutboxHelper.save(orderRestaurantOutboxMessage);
    log.info("RestaurantApprovalOutboxMessage is updated with outbox status: {}", outboxStatus.name());
  }
}
