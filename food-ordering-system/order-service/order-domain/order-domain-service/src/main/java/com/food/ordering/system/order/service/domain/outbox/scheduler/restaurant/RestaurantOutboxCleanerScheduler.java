package com.food.ordering.system.order.service.domain.outbox.scheduler.restaurant;

import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantOutboxMessage;
import com.food.ordering.system.outbox.OutboxScheduler;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RestaurantOutboxCleanerScheduler implements OutboxScheduler {

  private final RestaurantOutboxHelper restaurantOutboxHelper;

  public RestaurantOutboxCleanerScheduler(RestaurantOutboxHelper restaurantOutboxHelper) {
    this.restaurantOutboxHelper = restaurantOutboxHelper;
  }

  @Override
  @Scheduled(cron = "@midnight")
  public void processOutboxMessage() {
    Optional<List<OrderRestaurantOutboxMessage>> outboxMessagesResponse =
      restaurantOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
        OutboxStatus.COMPLETED,
        SagaStatus.SUCCEEDED,
        SagaStatus.FAILED,
        SagaStatus.COMPENSATED);
    if (outboxMessagesResponse.isPresent()) {
      List<OrderRestaurantOutboxMessage> outboxMessages = outboxMessagesResponse.get();
      log.info("Received {} RestaurantApprovalOutboxMessage for clean-up. The payloads: {}",
        outboxMessages.size(),
        outboxMessages.stream()
          .map(OrderRestaurantOutboxMessage::getPayload)
          .collect(Collectors.joining("\n")));
      restaurantOutboxHelper.deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(
        OutboxStatus.COMPLETED,
        SagaStatus.SUCCEEDED,
        SagaStatus.FAILED,
        SagaStatus.COMPENSATED);
      log.info("{} RestaurantApprovalOutboxMessage deleted!", outboxMessages.size());
    }

  }
}
