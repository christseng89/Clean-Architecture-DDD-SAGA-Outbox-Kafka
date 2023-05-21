package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.domain.outbox.PaymentOrderStatus;
import com.food.ordering.system.domain.outbox.RestaurantOrderStatus;
import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderRequest;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.message.CustomerCreated;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.entity.*;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderProductEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantEventPayload;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Component
public class OrderDataMapper {

  public Restaurant restaurant(CreateOrderRequest createOrderRequest) {
    return Restaurant.builder()
      .restaurantId(new RestaurantId(createOrderRequest.getRestaurantId()))
      .products(createOrderRequest.getItems().stream()
        .map(orderItem -> new Product(new ProductId(orderItem.getProductId())))
        .toList()).build();
  }

  public Order order(CreateOrderRequest createOrderRequest) {
    return Order.builder()
      .customerId(new CustomerId(createOrderRequest.getCustomerId()))
      .restaurantId(new RestaurantId(createOrderRequest.getRestaurantId()))
      .deliveryAddress(streetAddress(createOrderRequest.getAddress()))
      .price(new Money(createOrderRequest.getPrice()))
      .items(orderItemList(createOrderRequest.getItems()))
      .build();
  }

  public CreateOrderResponse createOrderResponse(
    @Valid Order order, String message) {
    return CreateOrderResponse.builder()
      .orderTrackingId(order.getTrackingId().getValue())
      .orderStatus(order.getOrderStatus())
      .message(message)
      .build();
  }

  public TrackOrderResponse trackOrderResponse(
    @Valid Order order) {
    return TrackOrderResponse.builder()
      .orderTrackingId(order.getTrackingId().getValue())
      .orderStatus(order.getOrderStatus())
      .failureMessages(order.getFailureMessages())
      .build();
  }

  public OrderPaymentEventPayload orderPaymentEventCreatedPayload(
    OrderCreatedEvent orderCreatedEvent) {
    return OrderPaymentEventPayload.builder()
      .customerId(orderCreatedEvent.getOrder().getCustomerId().getValue().toString())
      .orderId(orderCreatedEvent.getOrder().getId().getValue().toString())
      .price(orderCreatedEvent.getOrder().getPrice().getAmount())
      .createdAt(orderCreatedEvent.getCreatedAt())
      .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
      .build();
  }

  public OrderPaymentEventPayload orderPaymentEventCancelledPayload(
    OrderCancelledEvent orderCancelledEvent) {
    return OrderPaymentEventPayload.builder()
      .customerId(orderCancelledEvent.getOrder().getCustomerId().getValue().toString())
      .orderId(orderCancelledEvent.getOrder().getId().getValue().toString())
      .price(orderCancelledEvent.getOrder().getPrice().getAmount())
      .createdAt(orderCancelledEvent.getCreatedAt())
      .paymentOrderStatus(PaymentOrderStatus.CANCELLED.name())
      .build();
  }

  public OrderRestaurantEventPayload orderRestaurantEventPayload(OrderPaidEvent orderPaidEvent) {
    return OrderRestaurantEventPayload.builder()
      .orderId(orderPaidEvent.getOrder().getId().getValue().toString())
      .restaurantId(orderPaidEvent.getOrder().getRestaurantId().getValue().toString())
      .restaurantOrderStatus(RestaurantOrderStatus.PAID.name())
      .products(orderPaidEvent.getOrder().getItems().stream()
        .map(orderItem -> OrderProductEventPayload.builder()
          .id(orderItem.getProduct().getId().getValue().toString())
          .quantity(orderItem.getQuantity())
          .build()).toList())
      .price(orderPaidEvent.getOrder().getPrice().getAmount())
      .createdAt(orderPaidEvent.getCreatedAt())
      .build();
  }

  public Customer customer(CustomerCreated customerCreated) {
    return new Customer(new CustomerId(UUID.fromString(customerCreated.getId())),
      customerCreated.getUsername(),
      customerCreated.getFirstName(),
      customerCreated.getLastName());
  }

  private List<OrderItem> orderItemList(
    List<com.food.ordering.system.order.service.domain.dto.create.OrderItem> orderItems) {
    return orderItems.stream()
      .map(orderItem -> OrderItem.builder()
        .product(new Product(new ProductId(orderItem.getProductId())))
        .price(new Money(orderItem.getPrice()))
        .quantity(orderItem.getQuantity())
        .subTotal(new Money(orderItem.getSubTotal()))
        .build()).toList();
  }

  private StreetAddress streetAddress(OrderAddress orderAddress) {
    return new StreetAddress(
      UUID.randomUUID(),
      orderAddress.getStreet(),
      orderAddress.getPostalCode(),
      orderAddress.getCity()
    );
  }
}
