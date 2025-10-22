package com.food.ordering.system.order.service.domain.dto.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderItem {
  @NotNull(message = "Product ID cannot be null")
  private final UUID productId;
  
  @NotNull(message = "Quantity cannot be null")
  @Min(value = 1, message = "Quantity must be at least 1")
  private final Integer quantity;
  
  @NotNull(message = "Price cannot be null")
  @DecimalMin(value = "0.01", message = "Price must be greater than 0")
  private final BigDecimal price;
  
  @NotNull(message = "Subtotal cannot be null")
  @DecimalMin(value = "0.01", message = "Subtotal must be greater than 0")
  private final BigDecimal subTotal;
}
