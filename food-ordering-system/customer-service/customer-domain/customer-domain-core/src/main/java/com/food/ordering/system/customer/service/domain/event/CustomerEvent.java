package com.food.ordering.system.customer.service.domain.event;

import com.food.ordering.system.customer.service.domain.entity.Customer;
import com.food.ordering.system.domain.event.DomainEvent;

import java.time.ZonedDateTime;

public abstract class CustomerEvent implements DomainEvent<Customer> {
  private final Customer customer;
  private final ZonedDateTime createdAt;

  protected CustomerEvent(Customer customer, ZonedDateTime createdAt) {
    this.customer = customer;
    this.createdAt = createdAt;
  }

  public Customer getCustomer() {
    return customer;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }
}
