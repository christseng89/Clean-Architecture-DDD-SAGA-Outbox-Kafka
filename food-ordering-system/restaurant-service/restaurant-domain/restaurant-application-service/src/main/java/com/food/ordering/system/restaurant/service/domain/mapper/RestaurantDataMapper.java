package com.food.ordering.system.restaurant.service.domain.mapper;

import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
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
public class RestaurantDataMapper {
  
  private static final Pattern UUID_PATTERN = Pattern.compile(
    "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
  );
  
  private static final BigDecimal MAX_PRICE = new BigDecimal("999999.99");
  private static final BigDecimal MIN_PRICE = new BigDecimal("0.01");
  public Restaurant restaurantApprovalRequestToRestaurant(
    RestaurantApprovalRequest restaurantApprovalRequest) {
    
    // Validate and sanitize input data
    validateAndSanitizeRequest(restaurantApprovalRequest);
    
    try {
      return Restaurant.builder()
        .restaurantId(new RestaurantId(parseSecureUUID(restaurantApprovalRequest.getRestaurantId())))
        .orderDetail(OrderDetail.builder()
          .orderId(new OrderId(parseSecureUUID(restaurantApprovalRequest.getOrderId())))
          .products(mapSecureProducts(restaurantApprovalRequest.getProducts()))
          .totalAmount(new Money(sanitizePrice(restaurantApprovalRequest.getPrice())))
          .orderStatus(OrderStatus.valueOf(restaurantApprovalRequest.getRestaurantOrderStatus().name()))
          .build())
        .build();
    } catch (Exception e) {
      log.error("Error mapping restaurant approval request: {}", e.getMessage());
      throw new IllegalArgumentException("Invalid restaurant approval request data", e);
    }
  }
  
  private void validateAndSanitizeRequest(RestaurantApprovalRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("Restaurant approval request cannot be null");
    }
    
    if (request.getProducts() == null || request.getProducts().isEmpty()) {
      throw new IllegalArgumentException("Products list cannot be null or empty");
    }
    
    if (request.getProducts().size() > 100) {
      throw new IllegalArgumentException("Too many products. Maximum allowed: 100");
    }
    
    if (request.getPrice() == null) {
      throw new IllegalArgumentException("Price cannot be null");
    }
  }
  
  private UUID parseSecureUUID(String uuidString) {
    if (uuidString == null || uuidString.trim().isEmpty()) {
      throw new IllegalArgumentException("UUID string cannot be null or empty");
    }
    
    String sanitized = uuidString.trim();
    if (!UUID_PATTERN.matcher(sanitized).matches()) {
      throw new IllegalArgumentException("Invalid UUID format: " + sanitized);
    }
    
    try {
      return UUID.fromString(sanitized);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid UUID: " + sanitized, e);
    }
  }
  
  private BigDecimal sanitizePrice(BigDecimal price) {
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
  
  private List<Product> mapSecureProducts(List<Product> products) {
    return products.stream()
      .limit(100) // Prevent resource exhaustion
      .map(product -> {
        if (product == null) {
          throw new IllegalArgumentException("Product cannot be null");
        }
        if (product.getQuantity() == null || product.getQuantity() < 1) {
          throw new IllegalArgumentException("Product quantity must be at least 1");
        }
        if (product.getQuantity() > 1000) {
          throw new IllegalArgumentException("Product quantity cannot exceed 1000");
        }
        
        return Product.builder()
          .productId(product.getId())
          .quantity(product.getQuantity())
          .build();
      })
      .collect(Collectors.toList());
  }

  public OrderEventPayload
  orderApprovalEventToOrderEventPayload(OrderApprovalEvent orderApprovalEvent) {
    if (orderApprovalEvent == null) {
      throw new IllegalArgumentException("Order approval event cannot be null");
    }
    
    try {
      return OrderEventPayload.builder()
        .orderId(sanitizeString(orderApprovalEvent.getOrderApproval().getOrderId().getValue().toString()))
        .restaurantId(sanitizeString(orderApprovalEvent.getRestaurantId().getValue().toString()))
        .orderApprovalStatus(sanitizeString(orderApprovalEvent.getOrderApproval().getApprovalStatus().name()))
        .createdAt(orderApprovalEvent.getCreatedAt())
        .failureMessages(sanitizeFailureMessages(orderApprovalEvent.getFailureMessages()))
        .build();
    } catch (Exception e) {
      log.error("Error mapping order approval event to payload: {}", e.getMessage());
      throw new IllegalArgumentException("Invalid order approval event data", e);
    }
  }
  
  private String sanitizeString(String input) {
    if (input == null) {
      return null;
    }
    
    // Remove potentially harmful characters
    return input.replaceAll("[<>\"'&;]", "")
                .replaceAll("\\p{Cntrl}", "") // Remove control characters
                .trim();
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
