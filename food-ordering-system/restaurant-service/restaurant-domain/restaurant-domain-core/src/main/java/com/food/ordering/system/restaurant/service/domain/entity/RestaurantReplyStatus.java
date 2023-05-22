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
  private final RestaurantStatus statusStatus;

  private RestaurantReplyStatus(Builder builder) {
    setId(builder.restaurantReplyStatusId);
    restaurantId = builder.restaurantId;
    orderId = builder.orderId;
    statusStatus = builder.statusStatus;
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

  public RestaurantStatus getStatusStatus() {
    return statusStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    RestaurantReplyStatus that = (RestaurantReplyStatus) o;
    return Objects.equals(restaurantId, that.restaurantId) && Objects.equals(orderId, that.orderId) && statusStatus == that.statusStatus;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), restaurantId, orderId, statusStatus);
  }

  public static final class Builder {
    private RestaurantReplyStatusId restaurantReplyStatusId;
    private RestaurantId restaurantId;
    private OrderId orderId;
    private RestaurantStatus statusStatus;

    private Builder() {
    }

    public Builder restaurantStatusId(RestaurantReplyStatusId val) {
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

    public Builder statusStatus(RestaurantStatus val) {
      statusStatus = val;
      return this;
    }

    public RestaurantReplyStatus build() {
      return new RestaurantReplyStatus(this);
    }
  }
}
