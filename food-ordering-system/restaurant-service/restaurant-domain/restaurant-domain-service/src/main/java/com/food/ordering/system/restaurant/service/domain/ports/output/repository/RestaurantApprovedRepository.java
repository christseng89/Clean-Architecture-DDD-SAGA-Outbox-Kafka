package com.food.ordering.system.restaurant.service.domain.ports.output.repository;

import com.food.ordering.system.restaurant.service.domain.entity.RestaurantApproved;

public interface RestaurantApprovedRepository {
  void save(RestaurantApproved restaurantApproved);
}
