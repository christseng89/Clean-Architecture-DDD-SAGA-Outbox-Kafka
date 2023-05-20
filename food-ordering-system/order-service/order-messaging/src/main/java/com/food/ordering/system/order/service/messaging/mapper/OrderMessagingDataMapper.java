package com.food.ordering.system.order.service.messaging.mapper;

import com.food.ordering.system.domain.outbox.RestaurantApprovalStatus;
import com.food.ordering.system.kafka.order.avro.model.*;
import com.food.ordering.system.order.service.domain.dto.message.CustomerCreated;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantResponse;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderMessagingDataMapper {

  public PaymentResponse paymentResponse(
    PaymentResponseAvroModel paymentResponseAvroModel) {
    return PaymentResponse.builder()
      .id(paymentResponseAvroModel.getId())
      .sagaId(paymentResponseAvroModel.getSagaId())
      .paymentId(paymentResponseAvroModel.getPaymentId())
      .customerId(paymentResponseAvroModel.getCustomerId())
      .orderId(paymentResponseAvroModel.getOrderId())
      .price(paymentResponseAvroModel.getPrice())
      .createdAt(paymentResponseAvroModel.getCreatedAt())
      .paymentStatus(com.food.ordering.system.domain.outbox.PaymentStatus.valueOf(
        paymentResponseAvroModel.getPaymentStatus().name()))
      .failureMessages(paymentResponseAvroModel.getFailureMessages())
      .build();
  }

  public RestaurantResponse restaurantResponse(
    RestaurantResponseAvroModel restaurantResponseAvroModel) {
    return RestaurantResponse.builder()
      .id(restaurantResponseAvroModel.getId())
      .sagaId(restaurantResponseAvroModel.getSagaId())
      .restaurantId(restaurantResponseAvroModel.getRestaurantId())
      .orderId(restaurantResponseAvroModel.getOrderId())
      .createdAt(restaurantResponseAvroModel.getCreatedAt())
      .restaurantApprovalStatus(RestaurantApprovalStatus.valueOf(
        restaurantResponseAvroModel.getRestaurantApprovalStatus().name()))
      .failureMessages(restaurantResponseAvroModel.getFailureMessages())
      .build();
  }

  public PaymentRequestAvroModel paymentRequestAvroModel(
    String sagaId, OrderPaymentEventPayload orderPaymentEventPayload) {
    return PaymentRequestAvroModel.newBuilder()
      .setId(UUID.randomUUID().toString())
      .setSagaId(sagaId)
      .setCustomerId(orderPaymentEventPayload.getCustomerId())
      .setOrderId(orderPaymentEventPayload.getOrderId())
      .setPrice(orderPaymentEventPayload.getPrice())
      .setCreatedAt(orderPaymentEventPayload.getCreatedAt().toInstant())
      .setPaymentOrderStatus(PaymentOrderStatus.valueOf(orderPaymentEventPayload.getPaymentOrderStatus()))
      .build();
  }

  public RestaurantRequestAvroModel restaurantApprovalRequestAvroModel(
    String sagaId, OrderRestaurantEventPayload orderRestaurantEventPayload) {
    return RestaurantRequestAvroModel.newBuilder()
      .setId(UUID.randomUUID().toString())
      .setSagaId(sagaId)
      .setOrderId(orderRestaurantEventPayload.getOrderId())
      .setRestaurantId(orderRestaurantEventPayload.getRestaurantId())
      .setRestaurantOrderStatus(RestaurantOrderStatus
        .valueOf(orderRestaurantEventPayload.getRestaurantOrderStatus()))
      .setProducts(orderRestaurantEventPayload.getProducts().stream()
        .map(restaurantApprovalEventProduct -> Product.newBuilder()
          .setId(restaurantApprovalEventProduct.getId())
          .setQuantity(restaurantApprovalEventProduct.getQuantity())
          .build()).toList())
      .setPrice(orderRestaurantEventPayload.getPrice())
      .setCreatedAt(orderRestaurantEventPayload.getCreatedAt().toInstant())
      .build();
  }

  public CustomerCreated customerRequest(CustomerAvroModel customerAvroModel) {
    return CustomerCreated.builder()
      .id(customerAvroModel.getId())
      .username(customerAvroModel.getUsername())
      .firstName(customerAvroModel.getFirstName())
      .lastName(customerAvroModel.getLastName())
      .build();
  }
}
