package com.food.ordering.system.restaurant.service.domain.ports.output.repository;

import com.food.ordering.system.restaurant.service.domain.entity.RestaurantReplyStatus;

public interface RestaurantStatusRepository {
  void save(RestaurantReplyStatus restaurantReplyStatus);
}
