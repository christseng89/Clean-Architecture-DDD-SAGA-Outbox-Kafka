package com.food.ordering.system.order.service.domain.outbox.model.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderRestaurantEventPayload {
  @JsonProperty
  private String orderId;
  @JsonProperty
  private String restaurantId;
  @JsonProperty
  private BigDecimal price;
  @JsonProperty
  private ZonedDateTime createdAt;
  @JsonProperty
  private String restaurantOrderStatus;
  @JsonProperty
  private List<OrderRestaurantEventProduct> products;

}
