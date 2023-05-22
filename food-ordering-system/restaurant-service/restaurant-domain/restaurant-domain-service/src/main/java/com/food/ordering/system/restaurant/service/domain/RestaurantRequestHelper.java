package com.food.ordering.system.restaurant.service.domain;

import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantRequest;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.event.RestaurantEvent;
import com.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.food.ordering.system.restaurant.service.domain.mapper.RestaurantDataMapper;
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.restaurant.service.domain.outbox.scheduler.OrderOutboxHelper;
import com.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.RestaurantResponseMessagePublisher;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantReplyStatusRepository;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class RestaurantRequestHelper {

  private final RestaurantDomainService restaurantDomainService;
  private final RestaurantDataMapper restaurantDataMapper;
  private final RestaurantRepository restaurantRepository;
  private final RestaurantReplyStatusRepository restaurantReplyStatusRepository;
  private final OrderOutboxHelper orderOutboxHelper;
  private final RestaurantResponseMessagePublisher restaurantResponseMessagePublisher;

  public RestaurantRequestHelper(
    RestaurantDomainService restaurantDomainService,
    RestaurantDataMapper restaurantDataMapper,
    RestaurantRepository restaurantRepository,
    RestaurantReplyStatusRepository restaurantReplyStatusRepository,
    OrderOutboxHelper orderOutboxHelper,
    RestaurantResponseMessagePublisher restaurantResponseMessagePublisher) {
    this.restaurantDomainService = restaurantDomainService;
    this.restaurantDataMapper = restaurantDataMapper;
    this.restaurantRepository = restaurantRepository;
    this.restaurantReplyStatusRepository = restaurantReplyStatusRepository;
    this.orderOutboxHelper = orderOutboxHelper;
    this.restaurantResponseMessagePublisher = restaurantResponseMessagePublisher;
  }

  @Transactional
  public void persistRestaurantResponse(RestaurantRequest restaurantRequest) {
    if (publishIfOutboxMessageProcessed(restaurantRequest)) {
      log.info("An outbox message with saga id: {} already saved to database!",
        restaurantRequest.getSagaId());
      return;
    }

    log.info("Processing restaurant status for order id: {}", restaurantRequest.getOrderId());
    List<String> failureMessages = new ArrayList<>();
    Restaurant restaurant = findRestaurant(restaurantRequest);
    RestaurantEvent restaurantApprovedEvent =
      restaurantDomainService.validateOrder(
        restaurant,
        failureMessages);
    restaurantReplyStatusRepository.save(restaurant.getRestaurantStatus());

    orderOutboxHelper
      .saveOrderOutboxMessage(restaurantDataMapper.orderEventPayload(restaurantApprovedEvent),
        restaurantApprovedEvent.getRestaurantStatus().getRestaurantStatus(),
        OutboxStatus.STARTED,
        UUID.fromString(restaurantRequest.getSagaId()));

  }

  private Restaurant findRestaurant(RestaurantRequest restaurantRequest) {
    Restaurant restaurant = restaurantDataMapper
      .restaurant(restaurantRequest);
    Optional<Restaurant> restaurantResult = restaurantRepository.findRestaurantInformation(restaurant);
    if (restaurantResult.isEmpty()) {
      log.error("Restaurant with id " + restaurant.getId().getValue() + " not found!");
      throw new RestaurantNotFoundException("Restaurant with id " + restaurant.getId().getValue() +
        " not found!");
    }

    Restaurant restaurantEntity = restaurantResult.get();
    restaurant.setActive(restaurantEntity.isActive());
    restaurant.getOrderDetail().getProducts().forEach(product ->
      restaurantEntity.getOrderDetail().getProducts().forEach(p -> {
        if (p.getId().equals(product.getId())) {
          product.updateWithConfirmedNamePriceAndAvailability(p.getName(), p.getPrice(), p.isAvailable());
        }
      }));
    restaurant.getOrderDetail().setId(new OrderId(UUID.fromString(restaurantRequest.getOrderId())));

    return restaurant;
  }

  private boolean publishIfOutboxMessageProcessed(RestaurantRequest restaurantRequest) {
    Optional<OrderOutboxMessage> orderOutboxMessage =
      orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(UUID
        .fromString(restaurantRequest.getSagaId()), OutboxStatus.COMPLETED);
    if (orderOutboxMessage.isPresent()) {
      restaurantResponseMessagePublisher.publish(orderOutboxMessage.get(),
        orderOutboxHelper::updateOutboxStatus);
      return true;
    }
    return false;
  }
}
