package com.food.ordering.system.order.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.*;
import com.food.ordering.system.order.service.domain.dto.message.CustomerMessage;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantResponse;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderMessagingDataMapper {

  public PaymentResponse paymentResponseAvroModelToPaymentResponse(
    PaymentResponseAvroModel paymentResponseAvroModel) {
    return PaymentResponse.builder()
      .id(paymentResponseAvroModel.getId())
      .sagaId(paymentResponseAvroModel.getSagaId())
      .paymentId(paymentResponseAvroModel.getPaymentId())
      .customerId(paymentResponseAvroModel.getCustomerId())
      .orderId(paymentResponseAvroModel.getOrderId())
      .price(paymentResponseAvroModel.getPrice())
      .createdAt(paymentResponseAvroModel.getCreatedAt())
      .paymentStatus(com.food.ordering.system.domain.valueobject.PaymentStatus.valueOf(
        paymentResponseAvroModel.getPaymentStatus().name()))
      .failureMessages(paymentResponseAvroModel.getFailureMessages())
      .build();
  }

  public RestaurantResponse
  approvalResponseAvroModelToApprovalResponse(
    RestaurantApprovalResponseAvroModel
      restaurantApprovalResponseAvroModel) {
    return RestaurantResponse.builder()
      .id(restaurantApprovalResponseAvroModel.getId())
      .sagaId(restaurantApprovalResponseAvroModel.getSagaId())
      .restaurantId(restaurantApprovalResponseAvroModel.getRestaurantId())
      .orderId(restaurantApprovalResponseAvroModel.getOrderId())
      .createdAt(restaurantApprovalResponseAvroModel.getCreatedAt())
      .orderApprovalStatus(com.food.ordering.system.domain.valueobject.OrderApprovalStatus.valueOf(
        restaurantApprovalResponseAvroModel.getOrderApprovalStatus().name()))
      .failureMessages(restaurantApprovalResponseAvroModel.getFailureMessages())
      .build();
  }

  public PaymentRequestAvroModel orderPaymentEventToPaymentRequestAvroModel(
    String sagaId, OrderPaymentEventPayload
    orderPaymentEventPayload) {
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

  public RestaurantApprovalRequestAvroModel
  orderApprovalEventToRestaurantApprovalRequestAvroModel(
    String sagaId, OrderRestaurantEventPayload
    orderRestaurantEventPayload) {
    return RestaurantApprovalRequestAvroModel.newBuilder()
      .setId(UUID.randomUUID().toString())
      .setSagaId(sagaId)
      .setOrderId(orderRestaurantEventPayload.getOrderId())
      .setRestaurantId(orderRestaurantEventPayload.getRestaurantId())
      .setRestaurantOrderStatus(RestaurantOrderStatus
        .valueOf(orderRestaurantEventPayload.getRestaurantOrderStatus()))
      .setProducts(orderRestaurantEventPayload.getProducts().stream()
        .map(orderApprovalEventProduct -> com.food.ordering.system.kafka.order.avro.model.Product.newBuilder()
          .setId(orderApprovalEventProduct.getId())
          .setQuantity(orderApprovalEventProduct.getQuantity())
          .build()).toList())
      .setPrice(orderRestaurantEventPayload.getPrice())
      .setCreatedAt(orderRestaurantEventPayload.getCreatedAt().toInstant())
      .build();
  }

  public CustomerMessage customerAvroModeltoCustomerModel(CustomerAvroModel customerAvroModel) {
    return CustomerMessage.builder()
      .id(customerAvroModel.getId())
      .username(customerAvroModel.getUsername())
      .firstName(customerAvroModel.getFirstName())
      .lastName(customerAvroModel.getLastName())
      .build();
  }
}
