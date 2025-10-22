package com.food.ordering.system.order.service.domain.dto.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
public class OrderAddress {
  @NotBlank(message = "Street cannot be blank")
  @Size(min = 5, max = 100, message = "Street must be between 5 and 100 characters")
  @Pattern(regexp = "^[a-zA-Z0-9\\s\\-,\\.]+$", message = "Street contains invalid characters")
  private final String street;
  
  @NotBlank(message = "Postal code cannot be blank")
  @Pattern(regexp = "^[0-9]{5,10}$", message = "Postal code must be 5-10 digits")
  private final String postalCode;
  
  @NotBlank(message = "City cannot be blank")
  @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
  @Pattern(regexp = "^[a-zA-Z\\s\\-]+$", message = "City contains invalid characters")
  private final String city;
}
