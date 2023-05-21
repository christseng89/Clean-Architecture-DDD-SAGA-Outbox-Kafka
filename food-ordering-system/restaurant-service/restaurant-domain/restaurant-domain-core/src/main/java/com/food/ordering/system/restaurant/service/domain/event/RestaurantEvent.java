package com.food.ordering.system.restaurant.service.domain.event;

import com.food.ordering.system.domain.event.DomainEvent;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantApproved;

import java.time.ZonedDateTime;
import java.util.List;

public abstract class RestaurantEvent implements DomainEvent<RestaurantApproved> {
  private final RestaurantApproved restaurantApproved;
  private final RestaurantId restaurantId;
  private final List<String> failureMessages;
  private final ZonedDateTime createdAt;

  protected RestaurantEvent(
    RestaurantApproved restaurantApproved,
    RestaurantId restaurantId,
    List<String> failureMessages,
    ZonedDateTime createdAt) {
    this.restaurantApproved = restaurantApproved;
    this.restaurantId = restaurantId;
    this.failureMessages = failureMessages;
    this.createdAt = createdAt;
  }

  public RestaurantApproved getRestaurantApproved() {
    return restaurantApproved;
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
