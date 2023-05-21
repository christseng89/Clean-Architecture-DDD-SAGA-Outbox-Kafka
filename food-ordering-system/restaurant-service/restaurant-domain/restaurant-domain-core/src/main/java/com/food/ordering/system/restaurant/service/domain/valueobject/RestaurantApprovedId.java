package com.food.ordering.system.restaurant.service.domain.valueobject;

import com.food.ordering.system.domain.valueobject.BaseId;

import java.util.UUID;

public class

RestaurantApprovedId extends BaseId<UUID> {
  public RestaurantApprovedId(UUID value) {
    super(value);
  }
}
