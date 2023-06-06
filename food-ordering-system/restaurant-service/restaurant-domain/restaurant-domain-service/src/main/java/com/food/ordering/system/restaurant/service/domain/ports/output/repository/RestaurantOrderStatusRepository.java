package com.food.ordering.system.restaurant.service.domain.ports.output.repository;

import com.food.ordering.system.restaurant.service.domain.entity.RestaurantOrderStatus;

public interface RestaurantOrderStatusRepository {
  void save(RestaurantOrderStatus restaurantOrderStatus);
}
