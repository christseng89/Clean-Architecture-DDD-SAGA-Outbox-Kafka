package com.food.ordering.system.customer.service.domain.event;

import com.food.ordering.system.customer.service.domain.entity.Customer;

import java.time.ZonedDateTime;

public class CustomerCreatedEvent extends CustomerEvent {
  public CustomerCreatedEvent(
    Customer customer,
    ZonedDateTime createdAt) {
    super(customer, createdAt);
  }
}
