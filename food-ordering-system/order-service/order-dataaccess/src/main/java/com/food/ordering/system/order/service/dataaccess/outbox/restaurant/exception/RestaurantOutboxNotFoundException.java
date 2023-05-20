package com.food.ordering.system.order.service.dataaccess.outbox.restaurant.exception;

public class RestaurantOutboxNotFoundException extends RuntimeException {

  public RestaurantOutboxNotFoundException(String message) {
    super(message);
  }
}
