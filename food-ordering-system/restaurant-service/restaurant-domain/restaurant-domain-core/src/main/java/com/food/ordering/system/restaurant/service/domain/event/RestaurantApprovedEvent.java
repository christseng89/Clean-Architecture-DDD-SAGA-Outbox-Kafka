package com.food.ordering.system.restaurant.service.domain.event;

import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class RestaurantApprovedEvent extends RestaurantEvent {

  public RestaurantApprovedEvent(
    RestaurantApproval restaurantApproval,
    RestaurantId restaurantId,
    List<String> failureMessages,
    ZonedDateTime createdAt) {
    super(restaurantApproval, restaurantId, failureMessages, createdAt);
  }

}
