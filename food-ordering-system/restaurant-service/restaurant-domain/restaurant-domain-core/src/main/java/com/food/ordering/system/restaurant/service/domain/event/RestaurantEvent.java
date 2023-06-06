package com.food.ordering.system.restaurant.service.domain.event;

import com.food.ordering.system.domain.event.DomainEvent;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantOrderStatus;

import java.time.ZonedDateTime;
import java.util.List;

public abstract class RestaurantEvent implements DomainEvent<RestaurantOrderStatus> {
  private final RestaurantOrderStatus restaurantOrderStatus;
  private final RestaurantId restaurantId;
  private final List<String> failureMessages;
  private final ZonedDateTime createdAt;

  protected RestaurantEvent(
    RestaurantOrderStatus restaurantOrderStatus,
    RestaurantId restaurantId,
    List<String> failureMessages,
    ZonedDateTime createdAt) {
    this.restaurantOrderStatus = restaurantOrderStatus;
    this.restaurantId = restaurantId;
    this.failureMessages = failureMessages;
    this.createdAt = createdAt;
  }

  public RestaurantOrderStatus getRestaurantStatus() {
    return restaurantOrderStatus;
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
