package com.food.ordering.system.customer.service.domain.ports.input.service;

import com.food.ordering.system.customer.service.domain.dto.CreateCustomerRequest;
import com.food.ordering.system.customer.service.domain.dto.CreateCustomerResponse;

import javax.validation.Valid;

public interface CustomerApplicationService {

  CreateCustomerResponse createCustomer(@Valid CreateCustomerRequest createCustomerRequest);

}
