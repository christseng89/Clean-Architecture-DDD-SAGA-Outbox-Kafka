package com.food.ordering.system.restaurant.service.domain.ports.output.repository;

import com.food.ordering.system.restaurant.service.domain.entity.RestaurantStatus;

public interface RestaurantStatusRepository {
  void save(RestaurantStatus restaurantStatus);
}
