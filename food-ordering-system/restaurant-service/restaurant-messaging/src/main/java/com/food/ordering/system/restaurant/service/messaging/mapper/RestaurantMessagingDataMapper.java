package com.food.ordering.system.restaurant.service.messaging.mapper;

import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantOrderStatus;
import com.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RestaurantMessagingDataMapper {

  private static final Pattern UUID_PATTERN = Pattern.compile(
    "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
  );
  
  private static final BigDecimal MAX_PRICE = new BigDecimal("999999.99");
  private static final BigDecimal MIN_PRICE = new BigDecimal("0.01");
  private static final int MAX_PRODUCTS = 100;
  private static final int MAX_QUANTITY = 1000;

  public RestaurantApprovalRequest
  restaurantApprovalRequestAvroModelToRestaurantApproval(
    RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel) {
    
    // Validate input to prevent DoS and resource exhaustion
    validateAvroModel(restaurantApprovalRequestAvroModel);
    
    try {
      return RestaurantApprovalRequest.builder()
        .id(sanitizeAndValidateUUID(restaurantApprovalRequestAvroModel.getId()))
        .sagaId(sanitizeAndValidateUUID(restaurantApprovalRequestAvroModel.getSagaId()))
        .restaurantId(sanitizeAndValidateUUID(restaurantApprovalRequestAvroModel.getRestaurantId()))
        .orderId(sanitizeAndValidateUUID(restaurantApprovalRequestAvroModel.getOrderId()))
        .restaurantOrderStatus(RestaurantOrderStatus.valueOf(
          sanitizeString(restaurantApprovalRequestAvroModel.getRestaurantOrderStatus().name())))
        .products(mapAndValidateProducts(restaurantApprovalRequestAvroModel.getProducts()))
        .price(validatePrice(restaurantApprovalRequestAvroModel.getPrice()))
        .createdAt(restaurantApprovalRequestAvroModel.getCreatedAt())
        .build();
    } catch (Exception e) {
      log.error("Error mapping restaurant approval request from Avro model: {}", e.getMessage());
      throw new IllegalArgumentException("Invalid restaurant approval request data", e);
    }
  }
  
  private void validateAvroModel(RestaurantApprovalRequestAvroModel model) {
    if (model == null) {
      throw new IllegalArgumentException("Restaurant approval request Avro model cannot be null");
    }
    
    if (model.getProducts() == null || model.getProducts().isEmpty()) {
      throw new IllegalArgumentException("Products list cannot be null or empty");
    }
    
    if (model.getProducts().size() > MAX_PRODUCTS) {
      throw new IllegalArgumentException("Too many products. Maximum allowed: " + MAX_PRODUCTS);
    }
    
    if (model.getPrice() == null) {
      throw new IllegalArgumentException("Price cannot be null");
    }
  }
  
  private String sanitizeAndValidateUUID(String uuidString) {
    if (uuidString == null || uuidString.trim().isEmpty()) {
      throw new IllegalArgumentException("UUID string cannot be null or empty");
    }
    
    String sanitized = sanitizeString(uuidString);
    if (!UUID_PATTERN.matcher(sanitized).matches()) {
      throw new IllegalArgumentException("Invalid UUID format: " + sanitized);
    }
    
    try {
      UUID.fromString(sanitized); // Validate it's a proper UUID
      return sanitized;
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid UUID: " + sanitized, e);
    }
  }
  
  private String sanitizeString(String input) {
    if (input == null) {
      return null;
    }
    
    // Remove potentially harmful characters and control characters
    return input.replaceAll("[<>\"'&;]", "")
                .replaceAll("\\p{Cntrl}", "")
                .trim();
  }
  
  private BigDecimal validatePrice(BigDecimal price) {
    if (price == null) {
      throw new IllegalArgumentException("Price cannot be null");
    }
    
    if (price.compareTo(MIN_PRICE) < 0) {
      throw new IllegalArgumentException("Price cannot be less than " + MIN_PRICE);
    }
    
    if (price.compareTo(MAX_PRICE) > 0) {
      throw new IllegalArgumentException("Price cannot be greater than " + MAX_PRICE);
    }
    
    if (price.scale() > 2) {
      throw new IllegalArgumentException("Price cannot have more than 2 decimal places");
    }
    
    return price.setScale(2, BigDecimal.ROUND_HALF_UP);
  }
  
  private List<Product> mapAndValidateProducts(List<com.food.ordering.system.kafka.order.avro.model.Product> products) {
    return products.stream()
      .limit(MAX_PRODUCTS) // Prevent resource exhaustion
      .map(avroModel -> {
        if (avroModel == null) {
          throw new IllegalArgumentException("Product cannot be null");
        }
        
        if (avroModel.getId() == null) {
          throw new IllegalArgumentException("Product ID cannot be null");
        }
        
        if (avroModel.getQuantity() == null || avroModel.getQuantity() < 1) {
          throw new IllegalArgumentException("Product quantity must be at least 1");
        }
        
        if (avroModel.getQuantity() > MAX_QUANTITY) {
          throw new IllegalArgumentException("Product quantity cannot exceed " + MAX_QUANTITY);
        }
        
        return Product.builder()
          .productId(new ProductId(UUID.fromString(sanitizeAndValidateUUID(avroModel.getId()))))
          .quantity(avroModel.getQuantity())
          .build();
      })
      .collect(Collectors.toList());
  }

  public RestaurantApprovalResponseAvroModel
  orderEventPayloadToRestaurantApprovalResponseAvroModel(String sagaId, OrderEventPayload orderEventPayload) {
    
    // Validate inputs
    if (sagaId == null || sagaId.trim().isEmpty()) {
      throw new IllegalArgumentException("Saga ID cannot be null or empty");
    }
    
    if (orderEventPayload == null) {
      throw new IllegalArgumentException("Order event payload cannot be null");
    }
    
    try {
      return RestaurantApprovalResponseAvroModel.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setSagaId(sanitizeAndValidateUUID(sagaId))
        .setOrderId(sanitizeString(orderEventPayload.getOrderId()))
        .setRestaurantId(sanitizeString(orderEventPayload.getRestaurantId()))
        .setCreatedAt(orderEventPayload.getCreatedAt().toInstant())
        .setOrderApprovalStatus(OrderApprovalStatus.valueOf(
          sanitizeString(orderEventPayload.getOrderApprovalStatus())))
        .setFailureMessages(sanitizeFailureMessages(orderEventPayload.getFailureMessages()))
        .build();
    } catch (Exception e) {
      log.error("Error mapping order event payload to restaurant approval response: {}", e.getMessage());
      throw new IllegalArgumentException("Invalid order event payload data", e);
    }
  }
  
  private List<String> sanitizeFailureMessages(List<String> messages) {
    if (messages == null || messages.isEmpty()) {
      return messages;
    }
    
    return messages.stream()
      .limit(10) // Prevent resource exhaustion
      .map(this::sanitizeString)
      .filter(msg -> msg != null && !msg.isEmpty())
      .collect(Collectors.toList());
  }
}
