package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderRequest;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderCreateHelper {

  private final OrderDomainService orderDomainService;

  private final OrderRepository orderRepository;

  private final CustomerRepository customerRepository;

  private final RestaurantRepository restaurantRepository;

  private final OrderDataMapper orderDataMapper;

  public OrderCreateHelper(
    OrderDomainService orderDomainService,
    OrderRepository orderRepository,
    CustomerRepository customerRepository,
    RestaurantRepository restaurantRepository,
    OrderDataMapper orderDataMapper) {
    this.orderDomainService = orderDomainService;
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
    this.restaurantRepository = restaurantRepository;
    this.orderDataMapper = orderDataMapper;
  }

  @Transactional
  public OrderCreatedEvent persistOrder(CreateOrderRequest createOrderRequest) {
    checkCustomer(createOrderRequest.getCustomerId());
    Restaurant restaurant = checkRestaurant(createOrderRequest);
    Order order = orderDataMapper.order(createOrderRequest);
    OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);

    saveOrder(order);

    log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());
    return orderCreatedEvent;
  }

  private Restaurant checkRestaurant(CreateOrderRequest createOrderRequest) {
    Restaurant restaurant = orderDataMapper.restaurant(createOrderRequest);
    Optional<Restaurant> optionalRestaurant = restaurantRepository.findRestaurant(restaurant);

    if (optionalRestaurant.isEmpty()) {
      log.warn("Could not find restaurant with restaurant id: {}", createOrderRequest.getRestaurantId());
      throw new OrderDomainException("Could not find restaurant with restaurant id: " +
        createOrderRequest.getRestaurantId());
    }

    return optionalRestaurant.get();
  }

  private void checkCustomer(UUID customerId) {
    Optional<Customer> customer = customerRepository.findCustomerById(customerId);

    if (customer.isEmpty()) {
      log.warn("Could not find customer with customer id: {}", customerId);
      throw new OrderDomainException("Could not find customer with customer id: " + customer);
    }
  }

  private void saveOrder(Order order) throws OrderDomainException {
    Order orderResult = orderRepository.save(order);
    
    if (orderResult == null) {
      log.error("Could not save order!");
      throw new OrderDomainException("Could not save order!");
    }
    log.info("Order is saved with id: {}", orderResult.getId().getValue());
  }
}
