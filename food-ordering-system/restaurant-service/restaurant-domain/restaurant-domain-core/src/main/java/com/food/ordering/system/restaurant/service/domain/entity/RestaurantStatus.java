package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.valueobject.RestaurantStatusId;

import java.util.Objects;

public class RestaurantStatus extends BaseEntity<RestaurantStatusId> {
  private final RestaurantId restaurantId;
  private final OrderId orderId;
  private final com.food.ordering.system.domain.outbox.RestaurantStatus statusStatus;

  private RestaurantStatus(Builder builder) {
    setId(builder.restaurantStatusId);
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

  public com.food.ordering.system.domain.outbox.RestaurantStatus getStatusStatus() {
    return statusStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    RestaurantStatus that = (RestaurantStatus) o;
    return Objects.equals(restaurantId, that.restaurantId) && Objects.equals(orderId, that.orderId) && statusStatus == that.statusStatus;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), restaurantId, orderId, statusStatus);
  }

  public static final class Builder {
    private RestaurantStatusId restaurantStatusId;
    private RestaurantId restaurantId;
    private OrderId orderId;
    private com.food.ordering.system.domain.outbox.RestaurantStatus statusStatus;

    private Builder() {
    }

    public Builder restaurantStatusId(RestaurantStatusId val) {
      restaurantStatusId = val;
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

    public Builder statusStatus(com.food.ordering.system.domain.outbox.RestaurantStatus val) {
      statusStatus = val;
      return this;
    }

    public RestaurantStatus build() {
      return new RestaurantStatus(this);
    }
  }
}
