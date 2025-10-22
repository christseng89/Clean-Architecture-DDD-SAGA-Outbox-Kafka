package com.food.ordering.system.restaurant.service.domain.dto;

import com.food.ordering.system.domain.valueobject.RestaurantOrderStatus;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
public class RestaurantApprovalRequest {
  @NotBlank(message = "ID cannot be blank")
  @Pattern(regexp = "^[a-fA-F0-9-]{36}$", message = "ID must be a valid UUID format")
  private String id;
  
  @NotBlank(message = "Saga ID cannot be blank")
  @Pattern(regexp = "^[a-fA-F0-9-]{36}$", message = "Saga ID must be a valid UUID format")
  private String sagaId;
  
  @NotBlank(message = "Restaurant ID cannot be blank")
  @Pattern(regexp = "^[a-fA-F0-9-]{36}$", message = "Restaurant ID must be a valid UUID format")
  private String restaurantId;
  
  @NotBlank(message = "Order ID cannot be blank")
  @Pattern(regexp = "^[a-fA-F0-9-]{36}$", message = "Order ID must be a valid UUID format")
  private String orderId;
  
  @NotNull(message = "Restaurant order status cannot be null")
  private RestaurantOrderStatus restaurantOrderStatus;
  
  @NotEmpty(message = "Products list cannot be empty")
  @Size(min = 1, max = 100, message = "Products list must contain between 1 and 100 items")
  @Valid
  private java.util.List<Product> products;
  
  @NotNull(message = "Price cannot be null")
  @DecimalMin(value = "0.01", message = "Price must be greater than 0")
  private java.math.BigDecimal price;
  
  @NotNull(message = "Created at timestamp cannot be null")
  private java.time.Instant createdAt;
}
