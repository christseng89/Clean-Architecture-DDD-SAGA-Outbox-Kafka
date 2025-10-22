package com.food.ordering.system.order.service.domain.dto.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CreateOrderCommand {
  @NotNull(message = "Customer ID cannot be null")
  private final UUID customerId;
  
  @NotNull(message = "Restaurant ID cannot be null")
  private final UUID restaurantId;
  
  @NotNull(message = "Price cannot be null")
  @DecimalMin(value = "0.01", message = "Price must be greater than 0")
  private final BigDecimal price;
  
  @NotEmpty(message = "Order items cannot be empty")
  @Size(min = 1, max = 50, message = "Order must contain between 1 and 50 items")
  @Valid
  private final List<OrderItem> items;
  
  @NotNull(message = "Address cannot be null")
  @Valid
  private final OrderAddress address;
}
