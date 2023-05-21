package com.food.ordering.system.customer.service.domain;

import com.food.ordering.system.customer.service.domain.dto.CreateCustomerRequest;
import com.food.ordering.system.customer.service.domain.dto.CreateCustomerResponse;
import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.food.ordering.system.customer.service.domain.mapper.CustomerDataMapper;
import com.food.ordering.system.customer.service.domain.ports.input.service.CustomerApplicationService;
import com.food.ordering.system.customer.service.domain.ports.output.message.publisher.CustomerMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
class CustomerApplicationServiceImpl implements CustomerApplicationService {

  private final CustomerCreateHandler customerCreateHandler;

  private final CustomerDataMapper customerDataMapper;

  private final CustomerMessagePublisher customerMessagePublisher;

  public CustomerApplicationServiceImpl(
    CustomerCreateHandler customerCreateHandler,
    CustomerDataMapper customerDataMapper,
    CustomerMessagePublisher customerMessagePublisher) {
    this.customerCreateHandler = customerCreateHandler;
    this.customerDataMapper = customerDataMapper;
    this.customerMessagePublisher = customerMessagePublisher;
  }

  @Override
  public CreateCustomerResponse createCustomerResponse(CreateCustomerRequest createCustomerRequest) {
    CustomerCreatedEvent customerCreatedEvent = customerCreateHandler.createCustomerResponse(createCustomerRequest);
    customerMessagePublisher.publish(customerCreatedEvent);
    return customerDataMapper
      .customerToCreateCustomerResponse(customerCreatedEvent.getCustomer(),
        "Customer saved successfully @ " + customerCreatedEvent.getCreatedAt());
  }
}
