package com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurant;

import com.food.ordering.system.order.service.domain.dto.message.RestaurantResponse;

public interface RestaurantResponseMessageListener {
  void orderStatus(RestaurantResponse restaurantResponse);

  void orderRejected(RestaurantResponse restaurantResponse);
}
