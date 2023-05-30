package com.food.ordering.system.restaurant.service.domain.ports.output.repository;

import com.food.ordering.system.restaurant.service.domain.entity.RestaurantRespStatus;

public interface RestaurantRespStatusRepository {
  void save(RestaurantRespStatus restaurantRespStatus);
}
