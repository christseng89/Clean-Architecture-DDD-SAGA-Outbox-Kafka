package com.food.ordering.system.restaurant.service.domain.event;

import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantStatus;

import java.time.ZonedDateTime;
import java.util.List;

public class RestaurantRejectedEvent extends RestaurantEvent {

  public RestaurantRejectedEvent(
    RestaurantStatus restaurantStatus,
    RestaurantId restaurantId,
    List<String> failureMessages,
    ZonedDateTime createdAt) {
    super(restaurantStatus, restaurantId, failureMessages, createdAt);
  }

}
