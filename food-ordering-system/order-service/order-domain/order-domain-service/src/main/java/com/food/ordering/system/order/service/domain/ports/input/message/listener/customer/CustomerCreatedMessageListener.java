package com.food.ordering.system.order.service.domain.ports.input.message.listener.customer;

import com.food.ordering.system.order.service.domain.dto.message.CustomerCreated;

public interface CustomerCreatedMessageListener {

  void customerCreated(CustomerCreated customerCreated);
}
