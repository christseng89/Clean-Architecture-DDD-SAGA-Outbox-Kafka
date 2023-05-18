package com.food.ordering.system.customer.service.application.rest;

import com.food.ordering.system.customer.service.domain.dto.CreateCustomer;
import com.food.ordering.system.customer.service.domain.dto.CreateCustomerResponse;
import com.food.ordering.system.customer.service.domain.ports.input.service.CustomerApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/customers", produces = "application/vnd.api.v1+json")
public class CustomerController {
  private final CustomerApplicationService customerApplicationService;

  public CustomerController(CustomerApplicationService customerApplicationService) {
    this.customerApplicationService = customerApplicationService;
  }

  @PostMapping
  public ResponseEntity<CreateCustomerResponse> createCustomer(
    @RequestBody CreateCustomer createCustomer) {
    log.info("Creating customer with username: {}", createCustomer.getUsername());

    CreateCustomerResponse response = customerApplicationService.
      createCustomer(createCustomer);
    return ResponseEntity.ok(response);
  }

}
