package com.food.ordering.system.customer.service.domain;

import com.food.ordering.system.customer.service.domain.dto.CreateCustomerRequest;
import com.food.ordering.system.customer.service.domain.entity.Customer;
import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.food.ordering.system.customer.service.domain.exception.CustomerDomainException;
import com.food.ordering.system.customer.service.domain.mapper.CustomerDataMapper;
import com.food.ordering.system.customer.service.domain.ports.output.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
class CustomerCreateHandler {

  private final CustomerDomainService customerDomainService;

  private final CustomerRepository customerRepository;

  private final CustomerDataMapper customerDataMapper;

  public CustomerCreateHandler(
    CustomerDomainService customerDomainService,
    CustomerRepository customerRepository,
    CustomerDataMapper customerDataMapper) {
    this.customerDomainService = customerDomainService;
    this.customerRepository = customerRepository;
    this.customerDataMapper = customerDataMapper;
  }

  @Transactional
  public CustomerCreatedEvent createCustomerResponse(CreateCustomerRequest createCustomerRequest) {
    Customer customer = customerDataMapper.createCustomerCommandToCustomer(createCustomerRequest);
    CustomerCreatedEvent customerCreatedEvent = customerDomainService.validateAndInitiateCustomer(customer);
    Customer savedCustomer = customerRepository.createCustomerResponse(customer);
    if (savedCustomer == null) {
      log.error("Could not save customer with id: {}", createCustomerRequest.getCustomerId());
      throw new CustomerDomainException("Could not save customer with id " +
        createCustomerRequest.getCustomerId());
    }
    log.info("Returning CustomerCreatedEvent for customer id: {}", createCustomerRequest.getCustomerId());
    return customerCreatedEvent;
  }
}
