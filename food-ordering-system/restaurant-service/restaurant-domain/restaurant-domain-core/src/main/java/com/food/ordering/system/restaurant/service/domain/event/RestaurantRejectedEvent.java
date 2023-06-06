package com.food.ordering.system.restaurant.service.domain.event;

import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantOrderStatus;

import java.time.ZonedDateTime;
import java.util.List;

public class RestaurantRejectedEvent extends RestaurantEvent {

  public RestaurantRejectedEvent(
    RestaurantOrderStatus restaurantOrderStatus,
    RestaurantId restaurantId,
    List<String> failureMessages,
    ZonedDateTime createdAt) {
    super(restaurantOrderStatus, restaurantId, failureMessages, createdAt);
  }

}
