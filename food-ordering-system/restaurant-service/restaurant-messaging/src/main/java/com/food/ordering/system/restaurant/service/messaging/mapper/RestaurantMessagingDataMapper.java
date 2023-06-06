package com.food.ordering.system.restaurant.service.messaging.mapper;

import com.food.ordering.system.domain.outbox.RestaurantOrderStatus;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.kafka.order.avro.model.RestaurantRequestAvroModel;
import com.food.ordering.system.kafka.order.avro.model.RestaurantResponseAvroModel;
import com.food.ordering.system.kafka.order.avro.model.RestaurantStatus;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantRequest;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RestaurantMessagingDataMapper {

  public RestaurantRequest
  restaurantStatusRequestAvroModelToRestaurantStatus(
    RestaurantRequestAvroModel restaurantRequestAvroModel) {
    return RestaurantRequest.builder()
      .id(restaurantRequestAvroModel.getId())
      .sagaId(restaurantRequestAvroModel.getSagaId())
      .restaurantId(restaurantRequestAvroModel.getRestaurantId())
      .orderId(restaurantRequestAvroModel.getOrderId())
      .restaurantOrderStatus(RestaurantOrderStatus.valueOf(restaurantRequestAvroModel
        .getRestaurantOrderStatus().name()))
      .products(restaurantRequestAvroModel.getProductAvroModels()
        .stream()
        .map(avroModel -> Product.builder()
          .productId(new ProductId(UUID.fromString(avroModel.getId())))
          .quantity(avroModel.getQuantity())
          .build())
        .toList())
      .price(restaurantRequestAvroModel.getPrice())
      .createdAt(restaurantRequestAvroModel.getCreatedAt())
      .build();
  }

  public RestaurantResponseAvroModel
  orderEventPayloadToRestaurantResponseAvroModel(String sagaId, OrderEventPayload orderEventPayload) {
    return RestaurantResponseAvroModel.newBuilder()
      .setId(UUID.randomUUID().toString())
      .setSagaId(sagaId)
      .setOrderId(orderEventPayload.getOrderId())
      .setRestaurantId(orderEventPayload.getRestaurantId())
      .setCreatedAt(orderEventPayload.getCreatedAt().toInstant())
      .setRestaurantOrderStatus(RestaurantStatus.valueOf(orderEventPayload.getRestaurantStatus()))
      .setFailureMessages(orderEventPayload.getFailureMessages())
      .build();
  }
}
