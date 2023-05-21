package com.food.ordering.system.restaurant.service.domain.event;

import com.food.ordering.system.domain.event.DomainEvent;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantStatus;

import java.time.ZonedDateTime;
import java.util.List;

public abstract class RestaurantEvent implements DomainEvent<RestaurantStatus> {
  private final RestaurantStatus restaurantStatus;
  private final RestaurantId restaurantId;
  private final List<String> failureMessages;
  private final ZonedDateTime createdAt;

  protected RestaurantEvent(
    RestaurantStatus restaurantStatus,
    RestaurantId restaurantId,
    List<String> failureMessages,
    ZonedDateTime createdAt) {
    this.restaurantStatus = restaurantStatus;
    this.restaurantId = restaurantId;
    this.failureMessages = failureMessages;
    this.createdAt = createdAt;
  }

  public RestaurantStatus getRestaurantStatus() {
    return restaurantStatus;
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
