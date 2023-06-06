package com.food.ordering.system.restaurant.service.domain.valueobject;

import com.food.ordering.system.domain.valueobject.BaseId;

import java.util.UUID;

public class

RestaurantOrderStatusId extends BaseId<UUID> {
  public RestaurantOrderStatusId(UUID value) {
    super(value);
  }
}
