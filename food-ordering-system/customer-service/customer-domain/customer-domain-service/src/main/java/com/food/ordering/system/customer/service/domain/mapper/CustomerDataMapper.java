package com.food.ordering.system.customer.service.domain.mapper;

import com.food.ordering.system.customer.service.domain.dto.CreateCustomerRequest;
import com.food.ordering.system.customer.service.domain.dto.CreateCustomerResponse;
import com.food.ordering.system.customer.service.domain.entity.Customer;
import com.food.ordering.system.domain.valueobject.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataMapper {

  public Customer createCustomerCommandToCustomer(
    CreateCustomerRequest createCustomerRequest) {
    return new Customer(
      new CustomerId(createCustomerRequest.getCustomerId()),
      createCustomerRequest.getUsername(),
      createCustomerRequest.getFirstName(),
      createCustomerRequest.getLastName());
  }

  public CreateCustomerResponse customerToCreateCustomerResponse(Customer customer, String message) {
    return new CreateCustomerResponse(customer.getId().getValue(), message);
  }
}
