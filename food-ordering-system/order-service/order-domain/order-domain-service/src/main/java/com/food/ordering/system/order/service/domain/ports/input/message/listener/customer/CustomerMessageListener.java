package com.food.ordering.system.order.service.domain.ports.input.message.listener.customer;

import com.food.ordering.system.order.service.domain.dto.message.CustomerMessage;

public interface CustomerMessageListener {

  void customerCreated(CustomerMessage customerMessage);
}
