package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderRequest;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderRequest;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
class OrderApplicationServiceImpl implements OrderApplicationService {

  private final OrderCreateHandler orderCreateHandler;

  private final OrderTrackHandler orderTrackHandler;

  public OrderApplicationServiceImpl(
    OrderCreateHandler orderCreateHandler,
    OrderTrackHandler orderTrackHandler) {
    this.orderCreateHandler = orderCreateHandler;
    this.orderTrackHandler = orderTrackHandler;
  }

  @Override
  public CreateOrderResponse createOrderResponse(CreateOrderRequest createOrderRequestCommand) {
    return orderCreateHandler.createOrderResponse(createOrderRequestCommand);
  }

  @Override
  public TrackOrderResponse trackOrderResponse(TrackOrderRequest trackOrderRequest) {
    return orderTrackHandler.trackOrderResponse(trackOrderRequest);
  }
}
