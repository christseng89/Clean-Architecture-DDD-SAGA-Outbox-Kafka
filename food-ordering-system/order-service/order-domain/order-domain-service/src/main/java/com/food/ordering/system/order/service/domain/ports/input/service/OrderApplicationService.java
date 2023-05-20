package com.food.ordering.system.order.service.domain.ports.input.service;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderRequest;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderRequest;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;

import javax.validation.Valid;

public interface OrderApplicationService {
  CreateOrderResponse createOrder(@Valid CreateOrderRequest createOrderRequestCommand);

  TrackOrderResponse trackOrder(@Valid TrackOrderRequest trackOrderRequest);
}
