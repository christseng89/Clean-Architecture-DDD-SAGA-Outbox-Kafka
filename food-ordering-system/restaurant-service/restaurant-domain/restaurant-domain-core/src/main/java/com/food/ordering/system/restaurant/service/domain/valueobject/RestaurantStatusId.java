package com.food.ordering.system.restaurant.service.domain.valueobject;

import com.food.ordering.system.domain.valueobject.BaseId;

import java.util.UUID;

public class

RestaurantStatusId extends BaseId<UUID> {
  public RestaurantStatusId(UUID value) {
    super(value);
  }
}
