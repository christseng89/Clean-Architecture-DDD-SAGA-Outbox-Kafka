package com.food.ordering.system.restaurant.service.domain.valueobject;

import com.food.ordering.system.domain.valueobject.BaseId;

import java.util.UUID;

public class

RestaurantApprovalId extends BaseId<UUID> {
  public RestaurantApprovalId(UUID value) {
    super(value);
  }
}
