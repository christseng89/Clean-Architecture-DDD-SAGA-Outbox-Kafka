package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.track.TrackOrderRequest;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
public class OrderTrackRequestHandler {

  private final OrderDataMapper orderDataMapper;

  private final OrderRepository orderRepository;

  public OrderTrackRequestHandler(
    OrderDataMapper orderDataMapper,
    OrderRepository orderRepository) {
    this.orderDataMapper = orderDataMapper;
    this.orderRepository = orderRepository;
  }

  @Transactional(readOnly = true)
  public TrackOrderResponse trackOrder(TrackOrderRequest trackOrderRequest) {
    Optional<Order> orderResult =
      orderRepository.findByTrackingId(new TrackingId(trackOrderRequest.getOrderTrackingId()));
    if (orderResult.isEmpty()) {
      log.warn("Could not find order with tracking id: {}", trackOrderRequest.getOrderTrackingId());
      throw new OrderDomainException("Could not find order with tracking id: " +
        trackOrderRequest.getOrderTrackingId());
    }
    return orderDataMapper.orderToTrackOrderResponse(orderResult.get());
  }
}
