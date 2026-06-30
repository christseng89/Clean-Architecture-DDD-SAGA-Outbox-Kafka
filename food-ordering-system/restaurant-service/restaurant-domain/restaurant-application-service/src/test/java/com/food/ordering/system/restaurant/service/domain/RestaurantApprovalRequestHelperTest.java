package com.food.ordering.system.restaurant.service.domain;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderOutboxRepository;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
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
      .products(List.of(requestProduct(PRODUCT_ID, 4)))
      .price(PRICE)
      .createdAt(Instant.now())
      .build();

    restaurantApprovalRequestWithInactiveRestaurant = RestaurantApprovalRequest.builder()
      .id(ORDER_ID.toString())
      .sagaId(SAGA_ID.toString())
      .restaurantId(RESTAURANT_ID.toString())
      .orderId(ORDER_ID.toString())
      .restaurantOrderStatus(com.food.ordering.system.domain.valueobject.RestaurantOrderStatus.PAID)
      .products(List.of(requestProduct(PRODUCT_ID, 4)))
      .price(PRICE)
      .createdAt(Instant.now())
      .build();

    Restaurant restaurant = Restaurant.builder()
      .restaurantId(new RestaurantId(RESTAURANT_ID))
      .orderDetail(OrderDetail.builder()
        .orderId(new OrderId(ORDER_ID))
        .products(List.of(restaurantProduct()))
        .totalAmount(new Money(PRICE))
        .orderStatus(OrderStatus.PAID)
        .build())
      .active(true)
      .build();

    Restaurant inactiveRestaurant = Restaurant.builder()
      .restaurantId(new RestaurantId(RESTAURANT_ID))
      .orderDetail(OrderDetail.builder()
        .orderId(new OrderId(ORDER_ID))
        .products(List.of(restaurantProduct()))
        .totalAmount(new Money(PRICE))
        .orderStatus(OrderStatus.PAID)
        .build())
      .active(false)
      .build();

    when(restaurantRepository.findRestaurantInformation(any(Restaurant.class)))
      .thenReturn(Optional.of(restaurant));
    when(orderOutboxRepository.save(any(OrderOutboxMessage.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));
    when(orderOutboxRepository.findByTypeAndSagaIdAndOutboxStatus(any(), any(), any()))
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
        .products(List.of(restaurantProduct()))
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
    List<Product> tooManyProducts = new ArrayList<>();
    for (int i = 0; i < 101; i++) {
      tooManyProducts.add(requestProduct(UUID.randomUUID(), 1));
    }

    RestaurantApprovalRequest requestWithTooManyProducts = RestaurantApprovalRequest.builder()
      .id(ORDER_ID.toString())
      .sagaId(SAGA_ID.toString())
      .restaurantId(RESTAURANT_ID.toString())
      .orderId(ORDER_ID.toString())
      .restaurantOrderStatus(com.food.ordering.system.domain.valueobject.RestaurantOrderStatus.PAID)
      .products(tooManyProducts)
      .price(PRICE)
      .createdAt(Instant.now())
      .build();

    ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
      () -> restaurantApprovalRequestHelper.persistOrderApproval(requestWithTooManyProducts));

    assertTrue(exception.getMessage().contains("Products list must contain between 1 and 100 items"));
  }

  @Test
  public void testApproveOrderWithInvalidPriceScale() {
    RestaurantApprovalRequest requestWithInvalidPrice = RestaurantApprovalRequest.builder()
      .id(ORDER_ID.toString())
      .sagaId(SAGA_ID.toString())
      .restaurantId(RESTAURANT_ID.toString())
      .orderId(ORDER_ID.toString())
      .restaurantOrderStatus(com.food.ordering.system.domain.valueobject.RestaurantOrderStatus.PAID)
      .products(List.of(requestProduct(PRODUCT_ID, 4)))
      .price(new BigDecimal("200.123")) // Invalid: more than 2 decimal places
      .createdAt(Instant.now())
      .build();

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
      () -> restaurantApprovalRequestHelper.persistOrderApproval(requestWithInvalidPrice));

    assertTrue(exception.getMessage().contains("decimal places"));
  }

  private Product requestProduct(UUID productId, int quantity) {
    return Product.builder()
      .productId(new ProductId(productId))
      .quantity(quantity)
      .build();
  }

  private Product restaurantProduct() {
    return Product.builder()
      .productId(new ProductId(PRODUCT_ID))
      .name("product-1")
      .price(new Money(PRODUCT_PRICE))
      .quantity(4)
      .available(true)
      .build();
  }
}
