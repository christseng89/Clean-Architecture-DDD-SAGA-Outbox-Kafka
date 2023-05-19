package com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurant;

import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantOutboxMessage;
import com.food.ordering.system.outbox.OutboxStatus;

import java.util.function.BiConsumer;

public interface RestaurantRequestMessagePublisher {

  void publish(
    OrderRestaurantOutboxMessage orderRestaurantOutboxMessage,
    BiConsumer<OrderRestaurantOutboxMessage, OutboxStatus> outboxCallback);
}
