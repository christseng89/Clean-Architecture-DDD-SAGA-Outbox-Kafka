package com.food.ordering.system.restaurant.service.domain.ports.input.message.listener;

import com.food.ordering.system.restaurant.service.domain.dto.RestaurantRequest;

public interface RestaurantRequestMessageListener {
  void approveOrder(RestaurantRequest restaurantRequest);
}
