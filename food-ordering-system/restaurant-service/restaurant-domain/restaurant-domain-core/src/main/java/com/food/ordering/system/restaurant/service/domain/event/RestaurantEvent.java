package com.food.ordering.system.restaurant.service.domain.event;

import com.food.ordering.system.domain.event.DomainEvent;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantReplyStatus;

import java.time.ZonedDateTime;
import java.util.List;

public abstract class RestaurantEvent implements DomainEvent<RestaurantReplyStatus> {
  private final RestaurantReplyStatus restaurantReplyStatus;
  private final RestaurantId restaurantId;
  private final List<String> failureMessages;
  private final ZonedDateTime createdAt;

  protected RestaurantEvent(
    RestaurantReplyStatus restaurantReplyStatus,
    RestaurantId restaurantId,
    List<String> failureMessages,
    ZonedDateTime createdAt) {
    this.restaurantReplyStatus = restaurantReplyStatus;
    this.restaurantId = restaurantId;
    this.failureMessages = failureMessages;
    this.createdAt = createdAt;
  }

  public RestaurantReplyStatus getRestaurantStatus() {
    return restaurantReplyStatus;
  }

  public RestaurantId getRestaurantId() {
    return restaurantId;
  }

  public List<String> getFailureMessages() {
    return failureMessages;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }
}
