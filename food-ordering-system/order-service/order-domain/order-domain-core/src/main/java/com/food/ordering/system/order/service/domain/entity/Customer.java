package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.valueobject.CustomerId;

import java.util.Objects;

public class Customer extends AggregateRoot<CustomerId> {

  private String username;
  private String firstName;
  private String lastName;

  public Customer(CustomerId customerId, String username, String firstName, String lastName) {
    super.setId(customerId);
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public Customer(CustomerId customerId) {
    super.setId(customerId);
  }

  public String getUsername() {
    return username;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Customer customer = (Customer) o;
    return Objects.equals(username, customer.username) && Objects.equals(firstName, customer.firstName) && Objects.equals(lastName, customer.lastName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), username, firstName, lastName);
  }
}
