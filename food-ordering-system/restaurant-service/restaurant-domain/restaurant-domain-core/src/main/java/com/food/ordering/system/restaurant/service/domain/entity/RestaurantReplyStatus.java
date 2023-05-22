package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.outbox.RestaurantStatus;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.valueobject.RestaurantReplyStatusId;

import java.util.Objects;

public class RestaurantReplyStatus extends BaseEntity<RestaurantReplyStatusId> {
  private final RestaurantId restaurantId;
  private final OrderId orderId;
  private final RestaurantStatus restaurantStatus;

  private RestaurantReplyStatus(Builder builder) {
    setId(builder.restaurantReplyStatusId);
    restaurantId = builder.restaurantId;
    orderId = builder.orderId;
    restaurantStatus = builder.restaurantStatus;
  }

  public static Builder builder() {
    return new Builder();
  }

  public RestaurantId getRestaurantId() {
    return restaurantId;
  }

  public OrderId getOrderId() {
    return orderId;
  }

  public RestaurantStatus getRestaurantStatus() {
    return restaurantStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    RestaurantReplyStatus that = (RestaurantReplyStatus) o;
    return Objects.equals(restaurantId, that.restaurantId) && Objects.equals(orderId, that.orderId) && restaurantStatus == that.restaurantStatus;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), restaurantId, orderId, restaurantStatus);
  }

  public static final class Builder {
    private RestaurantReplyStatusId restaurantReplyStatusId;
    private RestaurantId restaurantId;
    private OrderId orderId;
    private RestaurantStatus restaurantStatus;

    private Builder() {
    }

    public Builder restaurantReplyStatusId(RestaurantReplyStatusId val) {
      restaurantReplyStatusId = val;
      return this;
    }

    public Builder restaurantId(RestaurantId val) {
      restaurantId = val;
      return this;
    }

    public Builder orderId(OrderId val) {
      orderId = val;
      return this;
    }

    public Builder restaurantStatus(RestaurantStatus val) {
      restaurantStatus = val;
      return this;
    }

    public RestaurantReplyStatus build() {
      return new RestaurantReplyStatus(this);
    }
  }
}
