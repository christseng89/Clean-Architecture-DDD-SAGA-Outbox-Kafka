package com.food.ordering.system.customer.service.domain;

import com.food.ordering.system.customer.service.domain.create.CreateCustomerCommand;
import com.food.ordering.system.customer.service.domain.create.CreateCustomerResponse;
import com.food.ordering.system.customer.service.domain.entity.Customer;
import com.food.ordering.system.customer.service.domain.exception.CustomerDomainException;
import com.food.ordering.system.customer.service.domain.ports.input.service.CustomerApplicationService;
import com.food.ordering.system.customer.service.domain.ports.output.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = CustomerTestConfiguration.class)
public class CustomerApplicationServiceTest {

  private final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");

  @Autowired
  private CustomerApplicationService customerApplicationService;

  @Autowired
  private CustomerRepository customerRepository;

  private CreateCustomerCommand createCustomerCommand;
  private CreateCustomerCommand createCustomerCommandInvalidUsername;

  @BeforeAll
  public void init() {
    createCustomerCommand = CreateCustomerCommand.builder()
      .customerId(CUSTOMER_ID)
      .username("john_doe")
      .firstName("John")
      .lastName("Doe")
      .build();

    createCustomerCommandInvalidUsername = CreateCustomerCommand.builder()
      .customerId(CUSTOMER_ID)
      .username("")
      .firstName("John")
      .lastName("Doe")
      .build();

    Customer customer = new Customer();
    customer.setId(CUSTOMER_ID);
    customer.setUsername("john_doe");
    customer.setFirstName("John");
    customer.setLastName("Doe");

    when(customerRepository.createCustomer(any(Customer.class))).thenReturn(customer);
  }

  @Test
  public void testCreateCustomer() {
    CreateCustomerResponse createCustomerResponse = customerApplicationService.createCustomer(createCustomerCommand);
    assertNotNull(createCustomerResponse);
    assertEquals("Customer saved successfully!", createCustomerResponse.getMessage());
    assertNotNull(createCustomerResponse.getCustomerId());
  }

  @Test
  public void testCreateCustomerWithNullRepository() {
    when(customerRepository.createCustomer(any(Customer.class))).thenReturn(null);

    assertThrows(CustomerDomainException.class,
      () -> customerApplicationService.createCustomer(createCustomerCommand));
  }

  @Test
  public void testCreateCustomerWithValidation() {
    // Reset mock to return valid customer
    Customer customer = new Customer();
    customer.setId(CUSTOMER_ID);
    customer.setUsername("john_doe");
    customer.setFirstName("John");
    customer.setLastName("Doe");
    when(customerRepository.createCustomer(any(Customer.class))).thenReturn(customer);

    CreateCustomerResponse response = customerApplicationService.createCustomer(createCustomerCommand);
    assertNotNull(response);
    assertEquals(CUSTOMER_ID, response.getCustomerId());
  }
}
