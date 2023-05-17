package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {

  OrderCreatedEvent validateAndInitiateOrder(
    Order order, Restaurant restaurant); // NEW -> PENDING

  OrderPaidEvent payOrder(Order order); // PENDING -> PAID

  OrderCancelledEvent cancelOrderPayment(
    Order order,
    List<String> failureMessages); // PENDING -> CANCELLED

  void approveOrder(Order order);

  void cancelOrder(Order order, List<String> failureMessages);
}
