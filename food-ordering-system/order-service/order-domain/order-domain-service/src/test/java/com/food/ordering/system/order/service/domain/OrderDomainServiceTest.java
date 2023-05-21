package com.food.ordering.system.order.service.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.domain.outbox.OrderStatus;
import com.food.ordering.system.domain.outbox.PaymentOrderStatus;
import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderRequest;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.PaymentOutboxRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.system.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderDomainServiceTest {

  private final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
  private final UUID RESTAURANT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb45");
  private final UUID PRODUCT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb48");
  private final UUID ORDER_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afb");
  private final UUID SAGA_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afa");
  private final BigDecimal PRICE = new BigDecimal("200.00");
  @Autowired
  private OrderApplicationService orderApplicationService;
  @Autowired
  private OrderDataMapper orderDataMapper;
  @Autowired
  private OrderRepository orderRepository;
  @Autowired
  private CustomerRepository customerRepository;
  @Autowired
  private RestaurantRepository restaurantRepository;
  @Autowired
  private PaymentOutboxRepository paymentOutboxRepository;
  @Autowired
  private ObjectMapper objectMapper;
  private CreateOrderRequest createOrderRequest;
  private CreateOrderRequest createOrderRequestWrongPrice;
  private CreateOrderRequest createOrderRequestWrongProductPrice;

  @BeforeAll
  public void init() {
    createOrderRequest = CreateOrderRequest.builder()
      .customerId(CUSTOMER_ID)
      .restaurantId(RESTAURANT_ID)
      .address(OrderAddress.builder()
        .street("street_1")
        .postalCode("1000AB")
        .city("Paris")
        .build())
      .price(PRICE)
      .items(List.of(OrderItem.builder()
          .productId(PRODUCT_ID)
          .quantity(1)
          .price(new BigDecimal("50.00"))
          .subTotal(new BigDecimal("50.00"))
          .build(),
        OrderItem.builder()
          .productId(PRODUCT_ID)
          .quantity(3)
          .price(new BigDecimal("50.00"))
          .subTotal(new BigDecimal("150.00"))
          .build()))
      .build();

    createOrderRequestWrongPrice = CreateOrderRequest.builder()
      .customerId(CUSTOMER_ID)
      .restaurantId(RESTAURANT_ID)
      .address(OrderAddress.builder()
        .street("street_1")
        .postalCode("1000AB")
        .city("Paris")
        .build())
      .price(new BigDecimal("250.00"))
      .items(List.of(OrderItem.builder()
          .productId(PRODUCT_ID)
          .quantity(1)
          .price(new BigDecimal("50.00"))
          .subTotal(new BigDecimal("50.00"))
          .build(),
        OrderItem.builder()
          .productId(PRODUCT_ID)
          .quantity(3)
          .price(new BigDecimal("50.00"))
          .subTotal(new BigDecimal("150.00"))
          .build()))
      .build();

    createOrderRequestWrongProductPrice = CreateOrderRequest.builder()
      .customerId(CUSTOMER_ID)
      .restaurantId(RESTAURANT_ID)
      .address(OrderAddress.builder()
        .street("street_1")
        .postalCode("1000AB")
        .city("Paris")
        .build())
      .price(new BigDecimal("210.00"))
      .items(List.of(OrderItem.builder()
          .productId(PRODUCT_ID)
          .quantity(1)
          .price(new BigDecimal("60.00"))
          .subTotal(new BigDecimal("60.00"))
          .build(),
        OrderItem.builder()
          .productId(PRODUCT_ID)
          .quantity(3)
          .price(new BigDecimal("50.00"))
          .subTotal(new BigDecimal("150.00"))
          .build()))
      .build();

    Customer customer = new Customer(new CustomerId(CUSTOMER_ID));

    Restaurant restaurantResponse = Restaurant.builder()
      .restaurantId(new RestaurantId(createOrderRequest.getRestaurantId()))
      .products(List.of(
        new Product(new ProductId(PRODUCT_ID), "product-1",
          new Money(new BigDecimal("50.00"))),
        new Product(new ProductId(PRODUCT_ID), "product-2",
          new Money(new BigDecimal("50.00")))))
      .active(true)
      .build();

    Order order = orderDataMapper.order(createOrderRequest);
    order.setId(new OrderId(ORDER_ID));

    when(customerRepository.findCustomerById(CUSTOMER_ID))
      .thenReturn(Optional.of(customer));
    when(restaurantRepository
      .findRestaurant(orderDataMapper.restaurant(createOrderRequest)))
      .thenReturn(Optional.of(restaurantResponse));
    when(orderRepository.save(any(Order.class)))
      .thenReturn(order);
    when(paymentOutboxRepository
      .save(any(OrderPaymentOutboxMessage.class)))
      .thenReturn(getOrderPaymentOutboxMessage());
  }

  @Test
  public void testCreateOrder() {
    CreateOrderResponse createOrderResponse = orderApplicationService.createOrderResponse(createOrderRequest);
    assertEquals(createOrderResponse.getOrderStatus(), OrderStatus.PENDING);
    assertEquals(createOrderResponse.getMessage(), "Order created successfully");

    System.out.println("\nOrder created...\n");
    assertNotNull(createOrderResponse.getOrderTrackingId());
  }

  @Test
  public void testCreateOrderWithWrongTotalPrice() {
    OrderDomainException orderDomainException =
      assertThrows(OrderDomainException.class,
        () -> orderApplicationService.createOrderResponse(createOrderRequestWrongPrice));

    System.out.println("\nWrong total price...\n");
    assertEquals(orderDomainException.getMessage(),
      "Total price: 250.00 is not equal to Order items total: 200.00!");
  }

  @Test
  public void testCreateOrderWithWrongProductPrice() {
    OrderDomainException orderDomainException = assertThrows(
      OrderDomainException.class,
      () -> orderApplicationService.createOrderResponse(createOrderRequestWrongProductPrice));

    System.out.println("\nWrong product ID " + PRODUCT_ID + " price...\n");
    assertEquals(orderDomainException.getMessage(),
      "Order item price: 60.00 is not valid for product " + PRODUCT_ID);
  }

  @Test
  public void testCreateOrderWithPassiveRestaurant() {
    Restaurant restaurantResponse = Restaurant.builder()
      .restaurantId(new RestaurantId(createOrderRequest.getRestaurantId()))
      .products(List.of(
        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))))
      .active(false)
      .build();

    when(restaurantRepository
      .findRestaurant(orderDataMapper.restaurant(createOrderRequest)))
      .thenReturn(Optional.of(restaurantResponse));

    OrderDomainException orderDomainException = assertThrows(
      OrderDomainException.class,
      () -> orderApplicationService.createOrderResponse(createOrderRequest));

    System.out.println("\nInactive restaurant ID " + RESTAURANT_ID + "...\n");
    assertEquals(orderDomainException.getMessage(),
      "Restaurant with id " + RESTAURANT_ID + " is currently not active!");
  }

  private OrderPaymentOutboxMessage getOrderPaymentOutboxMessage() {
    OrderPaymentEventPayload orderPaymentEventPayload = OrderPaymentEventPayload.builder()
      .orderId(ORDER_ID.toString())
      .customerId(CUSTOMER_ID.toString())
      .price(PRICE)
      .createdAt(ZonedDateTime.now())
      .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
      .build();

    OrderPaymentOutboxMessage orderPaymentOutboxMessage = OrderPaymentOutboxMessage.builder()
      .id(UUID.randomUUID())
      .sagaId(SAGA_ID)
      .createdAt(ZonedDateTime.now())
      .type(ORDER_SAGA_NAME)
      .payload(createPayload(orderPaymentEventPayload))
      .orderStatus(OrderStatus.PENDING)
      .sagaStatus(SagaStatus.STARTED)
      .outboxStatus(OutboxStatus.STARTED)
      .version(0)
      .build();

    System.out.println("\nOrderPaymentOutboxMessage Payload..." + orderPaymentOutboxMessage.getPayload());
    return orderPaymentOutboxMessage;
  }

  private String createPayload(OrderPaymentEventPayload orderPaymentEventPayload) {
    try {
      return objectMapper.writeValueAsString(orderPaymentEventPayload);
    } catch (JsonProcessingException e) {
      throw new OrderDomainException("Cannot create OrderPaymentEventPayload object!");
    }
  }

}
