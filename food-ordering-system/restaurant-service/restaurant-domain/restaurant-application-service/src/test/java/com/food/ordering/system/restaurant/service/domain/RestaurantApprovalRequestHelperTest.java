package com.food.ordering.system.restaurant.service.domain;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.OrderApproval;
import com.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderOutboxRepository;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = RestaurantTestConfiguration.class)
public class RestaurantApprovalRequestHelperTest {

  private final UUID RESTAURANT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb45");
  private final UUID PRODUCT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb48");
  private final UUID ORDER_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afb");
  private final UUID SAGA_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afa");
  private final BigDecimal PRICE = new BigDecimal("200.00");
  private final BigDecimal PRODUCT_PRICE = new BigDecimal("50.00");

  @Autowired
  private RestaurantApprovalRequestHelper restaurantApprovalRequestHelper;

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Autowired
  private OrderApprovalRepository orderApprovalRepository;

  @Autowired
  private OrderOutboxRepository orderOutboxRepository;

  private RestaurantApprovalRequest restaurantApprovalRequest;
  private RestaurantApprovalRequest restaurantApprovalRequestWithInactiveRestaurant;

  @BeforeAll
  public void init() {
    restaurantApprovalRequest = RestaurantApprovalRequest.builder()
      .id(ORDER_ID.toString())
      .sagaId(SAGA_ID.toString())
      .restaurantId(RESTAURANT_ID.toString())
      .orderId(ORDER_ID.toString())
      .restaurantOrderStatus(com.food.ordering.system.domain.valueobject.RestaurantOrderStatus.PAID)
      .products(List.of(
        com.food.ordering.system.restaurant.service.domain.dto.Product.builder()
          .id(PRODUCT_ID.toString())
          .quantity(4)
          .build()
      ))
      .price(PRICE)
      .createdAt(ZonedDateTime.now())
      .build();

    restaurantApprovalRequestWithInactiveRestaurant = RestaurantApprovalRequest.builder()
      .id(ORDER_ID.toString())
      .sagaId(SAGA_ID.toString())
      .restaurantId(RESTAURANT_ID.toString())
      .orderId(ORDER_ID.toString())
      .restaurantOrderStatus(com.food.ordering.system.domain.valueobject.RestaurantOrderStatus.PAID)
      .products(List.of(
        com.food.ordering.system.restaurant.service.domain.dto.Product.builder()
          .id(PRODUCT_ID.toString())
          .quantity(4)
          .build()
      ))
      .price(PRICE)
      .createdAt(ZonedDateTime.now())
      .build();

    Restaurant restaurant = Restaurant.builder()
      .restaurantId(new RestaurantId(RESTAURANT_ID))
      .orderDetail(OrderDetail.builder()
        .orderId(new OrderId(ORDER_ID))
        .products(List.of(
          new Product(new ProductId(PRODUCT_ID), "product-1", new Money(PRODUCT_PRICE))
        ))
        .totalAmount(new Money(PRICE))
        .orderStatus(OrderStatus.PAID)
        .build())
      .active(true)
      .build();

    Restaurant inactiveRestaurant = Restaurant.builder()
      .restaurantId(new RestaurantId(RESTAURANT_ID))
      .orderDetail(OrderDetail.builder()
        .orderId(new OrderId(ORDER_ID))
        .products(List.of(
          new Product(new ProductId(PRODUCT_ID), "product-1", new Money(PRODUCT_PRICE))
        ))
        .totalAmount(new Money(PRICE))
        .orderStatus(OrderStatus.PAID)
        .build())
      .active(false)
      .build();

    when(restaurantRepository.findRestaurantInformation(any(Restaurant.class)))
      .thenReturn(Optional.of(restaurant));
    when(orderApprovalRepository.save(any(OrderApproval.class)))
      .thenReturn(OrderApproval.builder().build());
    when(orderOutboxRepository.findByTypeAndSagaIdAndSagaStatus(any(), any(), any()))
      .thenReturn(Optional.empty());
  }

  @Test
  public void testApproveOrder() {
    assertDoesNotThrow(() -> restaurantApprovalRequestHelper.persistOrderApproval(restaurantApprovalRequest));
  }

  @Test
  public void testApproveOrderWithInactiveRestaurant() {
    Restaurant inactiveRestaurant = Restaurant.builder()
      .restaurantId(new RestaurantId(RESTAURANT_ID))
      .orderDetail(OrderDetail.builder()
        .orderId(new OrderId(ORDER_ID))
        .products(List.of(
          new Product(new ProductId(PRODUCT_ID), "product-1", new Money(PRODUCT_PRICE))
        ))
        .totalAmount(new Money(PRICE))
        .orderStatus(OrderStatus.PAID)
        .build())
      .active(false)
      .build();

    when(restaurantRepository.findRestaurantInformation(any(Restaurant.class)))
      .thenReturn(Optional.of(inactiveRestaurant));

    assertDoesNotThrow(() -> restaurantApprovalRequestHelper.persistOrderApproval(restaurantApprovalRequestWithInactiveRestaurant));
  }

  @Test
  public void testApproveOrderWithNonExistentRestaurant() {
    when(restaurantRepository.findRestaurantInformation(any(Restaurant.class)))
      .thenReturn(Optional.empty());

    assertThrows(RestaurantNotFoundException.class,
      () -> restaurantApprovalRequestHelper.persistOrderApproval(restaurantApprovalRequest));
  }

  @Test
  public void testApproveOrderWithTooManyProducts() {
    List<com.food.ordering.system.restaurant.service.domain.dto.Product> tooManyProducts = new java.util.ArrayList<>();
    for (int i = 0; i < 101; i++) {
      tooManyProducts.add(
        com.food.ordering.system.restaurant.service.domain.dto.Product.builder()
          .id(UUID.randomUUID().toString())
          .quantity(1)
          .build()
      );
    }

    RestaurantApprovalRequest requestWithTooManyProducts = RestaurantApprovalRequest.builder()
      .id(ORDER_ID.toString())
      .sagaId(SAGA_ID.toString())
      .restaurantId(RESTAURANT_ID.toString())
      .orderId(ORDER_ID.toString())
      .restaurantOrderStatus(com.food.ordering.system.domain.valueobject.RestaurantOrderStatus.PAID)
      .products(tooManyProducts)
      .price(PRICE)
      .createdAt(ZonedDateTime.now())
      .build();

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
      () -> restaurantApprovalRequestHelper.persistOrderApproval(requestWithTooManyProducts));

    assertTrue(exception.getMessage().contains("Too many products"));
  }

  @Test
  public void testApproveOrderWithInvalidPriceScale() {
    RestaurantApprovalRequest requestWithInvalidPrice = RestaurantApprovalRequest.builder()
      .id(ORDER_ID.toString())
      .sagaId(SAGA_ID.toString())
      .restaurantId(RESTAURANT_ID.toString())
      .orderId(ORDER_ID.toString())
      .restaurantOrderStatus(com.food.ordering.system.domain.valueobject.RestaurantOrderStatus.PAID)
      .products(List.of(
        com.food.ordering.system.restaurant.service.domain.dto.Product.builder()
          .id(PRODUCT_ID.toString())
          .quantity(4)
          .build()
      ))
      .price(new BigDecimal("200.123")) // Invalid: more than 2 decimal places
      .createdAt(ZonedDateTime.now())
      .build();

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
      () -> restaurantApprovalRequestHelper.persistOrderApproval(requestWithInvalidPrice));

    assertTrue(exception.getMessage().contains("decimal places"));
  }
}
