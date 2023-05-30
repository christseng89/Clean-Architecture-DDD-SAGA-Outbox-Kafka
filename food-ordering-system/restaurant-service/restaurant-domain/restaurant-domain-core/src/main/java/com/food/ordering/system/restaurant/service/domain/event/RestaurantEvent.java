package com.food.ordering.system.restaurant.service.domain.event;

import com.food.ordering.system.domain.event.DomainEvent;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantRespStatus;

import java.time.ZonedDateTime;
import java.util.List;

public abstract class RestaurantEvent implements DomainEvent<RestaurantRespStatus> {
  private final RestaurantRespStatus restaurantRespStatus;
  private final RestaurantId restaurantId;
  private final List<String> failureMessages;
  private final ZonedDateTime createdAt;

  protected RestaurantEvent(
    RestaurantRespStatus restaurantRespStatus,
    RestaurantId restaurantId,
    List<String> failureMessages,
    ZonedDateTime createdAt) {
    this.restaurantRespStatus = restaurantRespStatus;
    this.restaurantId = restaurantId;
    this.failureMessages = failureMessages;
    this.createdAt = createdAt;
  }

  public RestaurantRespStatus getRestaurantStatus() {
    return restaurantRespStatus;
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
