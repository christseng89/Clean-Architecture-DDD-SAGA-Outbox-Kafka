package com.food.ordering.system.restaurant.service.domain;

import com.food.ordering.system.domain.outbox.RestaurantStatus;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.event.RestaurantApprovedEvent;
import com.food.ordering.system.restaurant.service.domain.event.RestaurantEvent;
import com.food.ordering.system.restaurant.service.domain.event.RestaurantRejectedEvent;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.food.ordering.system.domain.DomainConstants.UTC;

@Slf4j
public class RestaurantDomainServiceImpl implements RestaurantDomainService {

  @Override
  public RestaurantEvent validateOrder(Restaurant restaurant, List<String> failureMessages) {
    restaurant.validateOrder(failureMessages);
    log.info("Validating order with id: {}", restaurant.getOrderDetail().getId().getValue());

    if (failureMessages.isEmpty()) { // APPROVED
      log.info("Order is approved for order id: {}", restaurant.getOrderDetail().getId().getValue());
      restaurant.constructRestaurantApproved(RestaurantStatus.APPROVED);
      return new RestaurantApprovedEvent(
        restaurant.getRestaurantApproved(),
        restaurant.getId(),
        failureMessages,
        ZonedDateTime.now(ZoneId.of(UTC)));
    } else { // REJECTED
      log.info("Order is rejected for order id: {}", restaurant.getOrderDetail().getId().getValue());
      restaurant.constructRestaurantApproved(RestaurantStatus.REJECTED);
      return new RestaurantRejectedEvent(
        restaurant.getRestaurantApproved(),
        restaurant.getId(),
        failureMessages,
        ZonedDateTime.now(ZoneId.of(UTC)));
    }
  }
}
