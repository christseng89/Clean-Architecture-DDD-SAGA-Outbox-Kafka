package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderRequest;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.message.CustomerMessage;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.entity.*;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantEventProduct;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderDataMapper {

  public Restaurant createOrderCommandToRestaurant(CreateOrderRequest createOrderRequestCommand) {
    return Restaurant.builder()
      .restaurantId(new RestaurantId(createOrderRequestCommand.getRestaurantId()))
      .products(createOrderRequestCommand.getItems().stream()
        .map(orderItem -> new Product(new ProductId(orderItem.getProductId())))
        .toList()).build();
  }

  public Order createOrderCommandToOrder(CreateOrderRequest createOrderRequestCommand) {
    return Order.builder()
      .customerId(new CustomerId(createOrderRequestCommand.getCustomerId()))
      .restaurantId(new RestaurantId(createOrderRequestCommand.getRestaurantId()))
      .deliveryAddress(orderAddressToStreetAddress(createOrderRequestCommand.getAddress()))
      .price(new Money(createOrderRequestCommand.getPrice()))
      .items(orderItemsToOrderItemEntities(createOrderRequestCommand.getItems()))
      .build();
  }

  public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
    return CreateOrderResponse.builder()
      .orderTrackingId(order.getTrackingId().getValue())
      .orderStatus(order.getOrderStatus())
      .message(message)
      .build();
  }

  public TrackOrderResponse orderToTrackOrderResponse(Order order) {
    return TrackOrderResponse.builder()
      .orderTrackingId(order.getTrackingId().getValue())
      .orderStatus(order.getOrderStatus())
      .failureMessages(order.getFailureMessages())
      .build();
  }

  public OrderPaymentEventPayload orderCreatedEventToOrderPaymentEventPayload(OrderCreatedEvent orderCreatedEvent) {
    return OrderPaymentEventPayload.builder()
      .customerId(orderCreatedEvent.getOrder().getCustomerId().getValue().toString())
      .orderId(orderCreatedEvent.getOrder().getId().getValue().toString())
      .price(orderCreatedEvent.getOrder().getPrice().getAmount())
      .createdAt(orderCreatedEvent.getCreatedAt())
      .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
      .build();
  }

  public OrderPaymentEventPayload orderCancelledEventToOrderPaymentEventPayload(
    OrderCancelledEvent orderCancelledEvent) {
    return OrderPaymentEventPayload.builder()
      .customerId(orderCancelledEvent.getOrder().getCustomerId().getValue().toString())
      .orderId(orderCancelledEvent.getOrder().getId().getValue().toString())
      .price(orderCancelledEvent.getOrder().getPrice().getAmount())
      .createdAt(orderCancelledEvent.getCreatedAt())
      .paymentOrderStatus(PaymentOrderStatus.CANCELLED.name())
      .build();
  }

  public OrderRestaurantEventPayload orderPaidEventToOrderApprovalEventPayload(OrderPaidEvent orderPaidEvent) {
    return OrderRestaurantEventPayload.builder()
      .orderId(orderPaidEvent.getOrder().getId().getValue().toString())
      .restaurantId(orderPaidEvent.getOrder().getRestaurantId().getValue().toString())
      .restaurantOrderStatus(RestaurantOrderStatus.PAID.name())
      .products(orderPaidEvent.getOrder().getItems().stream()
        .map(orderItem -> OrderRestaurantEventProduct.builder()
          .id(orderItem.getProduct().getId().getValue().toString())
          .quantity(orderItem.getQuantity())
          .build()).toList())
      .price(orderPaidEvent.getOrder().getPrice().getAmount())
      .createdAt(orderPaidEvent.getCreatedAt())
      .build();
  }

  public Customer customerMessageToCustomer(CustomerMessage customerMessage) {
    return new Customer(new CustomerId(UUID.fromString(customerMessage.getId())),
      customerMessage.getUsername(),
      customerMessage.getFirstName(),
      customerMessage.getLastName());
  }

  private List<OrderItem> orderItemsToOrderItemEntities(
    List<com.food.ordering.system.order.service.domain.dto.create.OrderItem> orderItems) {
    return orderItems.stream()
      .map(orderItem -> OrderItem.builder()
        .product(new Product(new ProductId(orderItem.getProductId())))
        .price(new Money(orderItem.getPrice()))
        .quantity(orderItem.getQuantity())
        .subTotal(new Money(orderItem.getSubTotal()))
        .build()).toList();
  }

  private StreetAddress orderAddressToStreetAddress(OrderAddress orderAddress) {
    return new StreetAddress(
      UUID.randomUUID(),
      orderAddress.getStreet(),
      orderAddress.getPostalCode(),
      orderAddress.getCity()
    );
  }
}
