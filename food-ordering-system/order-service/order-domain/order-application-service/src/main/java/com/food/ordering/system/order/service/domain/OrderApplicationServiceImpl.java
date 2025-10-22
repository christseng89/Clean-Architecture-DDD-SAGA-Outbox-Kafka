package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Slf4j
@Validated
@Service
class OrderApplicationServiceImpl implements OrderApplicationService {

  private final OrderCreateCommandHandler orderCreateCommandHandler;

  private final OrderTrackCommandHandler orderTrackCommandHandler;

  public OrderApplicationServiceImpl(
    OrderCreateCommandHandler orderCreateCommandHandler,
    OrderTrackCommandHandler orderTrackCommandHandler) {
    this.orderCreateCommandHandler = orderCreateCommandHandler;
    this.orderTrackCommandHandler = orderTrackCommandHandler;
  }

  @Override
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  @RateLimiter(name = "create-order", fallbackMethod = "createOrderFallback")
  public CreateOrderResponse createOrder(@Valid @NotNull CreateOrderCommand createOrderCommand) {
    try {
      log.info("Processing order creation request for customer: {}", createOrderCommand.getCustomerId());
      return orderCreateCommandHandler.createOrder(createOrderCommand);
    } catch (Exception e) {
      log.error("Error processing order creation: ", e);
      throw e;
    }
  }

  @Override
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  @RateLimiter(name = "order-service", fallbackMethod = "trackOrderFallback")
  public TrackOrderResponse trackOrder(@Valid @NotNull TrackOrderQuery trackOrderQuery) {
    try {
      log.info("Processing order tracking request for order: {}", trackOrderQuery.getOrderTrackingId());
      return orderTrackCommandHandler.trackOrder(trackOrderQuery);
    } catch (Exception e) {
      log.error("Error processing order tracking: ", e);
      throw e;
    }
  }

  public CreateOrderResponse createOrderFallback(CreateOrderCommand createOrderCommand, Exception ex) {
    log.warn("Rate limit exceeded for order creation, customer: {}", createOrderCommand.getCustomerId());
    throw new RuntimeException("Service temporarily unavailable. Please try again later.", ex);
  }

  public TrackOrderResponse trackOrderFallback(TrackOrderQuery trackOrderQuery, Exception ex) {
    log.warn("Rate limit exceeded for order tracking, order: {}", trackOrderQuery.getOrderTrackingId());
    throw new RuntimeException("Service temporarily unavailable. Please try again later.", ex);
  }
}
