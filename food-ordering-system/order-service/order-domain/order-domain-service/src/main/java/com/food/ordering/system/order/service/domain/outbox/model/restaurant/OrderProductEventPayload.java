package com.food.ordering.system.order.service.domain.outbox.model.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderProductEventPayload {
  @JsonProperty
  private String id;
  @JsonProperty
  private Integer quantity;

}