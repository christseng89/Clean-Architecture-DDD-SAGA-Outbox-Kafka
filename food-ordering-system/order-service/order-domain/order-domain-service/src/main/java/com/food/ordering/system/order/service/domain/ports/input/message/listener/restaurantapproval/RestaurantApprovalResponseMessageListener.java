package com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval;

import com.food.ordering.system.order.service.domain.dto.message.RestaurantResponse;

public interface RestaurantApprovalResponseMessageListener {
  void orderApproved(RestaurantResponse restaurantResponse);

  void orderRejected(RestaurantResponse restaurantResponse);
}
