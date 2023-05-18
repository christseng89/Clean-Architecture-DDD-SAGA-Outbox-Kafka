package com.food.ordering.system.customer.service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CreateCustomer {
  @NotNull
  private final UUID customerId;
  @NotNull
  private final String username;
  @NotNull
  private final String firstName;
  @NotNull
  private final String lastName;
}
