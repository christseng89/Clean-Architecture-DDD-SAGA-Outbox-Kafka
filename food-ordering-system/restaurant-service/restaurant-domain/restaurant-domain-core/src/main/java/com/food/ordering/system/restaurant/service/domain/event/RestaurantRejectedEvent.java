package com.food.ordering.system.restaurant.service.domain.event;

import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantRespStatus;

import java.time.ZonedDateTime;
import java.util.List;

public class RestaurantRejectedEvent extends RestaurantEvent {

  public RestaurantRejectedEvent(
    RestaurantRespStatus restaurantRespStatus,
    RestaurantId restaurantId,
    List<String> failureMessages,
    ZonedDateTime createdAt) {
    super(restaurantRespStatus, restaurantId, failureMessages, createdAt);
  }

}
