package com.food.ordering.system.restaurant.service.domain;

import com.food.ordering.system.restaurant.service.domain.dto.RestaurantRequest;
import com.food.ordering.system.restaurant.service.domain.ports.input.message.listener.RestaurantRequestMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RestaurantRequestMessageListenerImpl implements RestaurantRequestMessageListener {

  private final RestaurantRequestHelper restaurantRequestHelper;

  public RestaurantRequestMessageListenerImpl(
    RestaurantRequestHelper restaurantRequestHelper) {
    this.restaurantRequestHelper = restaurantRequestHelper;
  }

  @Override
  public void approveOrder(RestaurantRequest restaurantRequest) {
    restaurantRequestHelper.persistOrderApproval(restaurantRequest);
  }
}
