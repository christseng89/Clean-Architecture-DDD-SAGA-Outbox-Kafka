package com.food.ordering.system.restaurant.service.domain.ports.output.repository;

import com.food.ordering.system.restaurant.service.domain.entity.RestaurantApproval;

public interface RestaurantApprovalRepository {
  void save(RestaurantApproval restaurantApproval);
}
