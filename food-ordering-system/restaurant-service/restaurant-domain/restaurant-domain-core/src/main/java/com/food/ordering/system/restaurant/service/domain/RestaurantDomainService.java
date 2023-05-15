package com.food.ordering.system.restaurant.service.domain;

import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.event.RestaurantEvent;

import java.util.List;

public interface RestaurantDomainService {

  RestaurantEvent validateOrder(Restaurant restaurant, List<String> failureMessages);
}
