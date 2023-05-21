package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.message.RestaurantResponse;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurant.RestaurantResponseMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Slf4j
@Validated
@Service
public class RestaurantResponseMessageListenerImpl implements RestaurantResponseMessageListener {

  private final OrderRestaurantSaga orderRestaurantSaga;

  public RestaurantResponseMessageListenerImpl(OrderRestaurantSaga orderRestaurantSaga) {
    this.orderRestaurantSaga = orderRestaurantSaga;
  }

  @Override
  public void orderApproved(RestaurantResponse restaurantResponse) {
    orderRestaurantSaga.process(restaurantResponse);
    log.info("Order is approved for order id: {}", restaurantResponse.getOrderId());
  }

  @Override
  public void orderRejected(RestaurantResponse restaurantResponse) {
    orderRestaurantSaga.rollback(restaurantResponse);
    log.info("Order Approved Saga rollback operation is completed for order id: {} with failure messages: {}",
      restaurantResponse.getOrderId(),
      String.join(FAILURE_MESSAGE_DELIMITER, restaurantResponse.getFailureMessages()));
  }
}
