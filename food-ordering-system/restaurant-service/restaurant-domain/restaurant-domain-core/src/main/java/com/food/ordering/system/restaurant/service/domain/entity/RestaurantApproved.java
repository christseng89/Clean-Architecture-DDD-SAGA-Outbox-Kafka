package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.outbox.RestaurantStatus;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.valueobject.RestaurantApprovedId;

import java.util.Objects;

public class RestaurantApproved extends BaseEntity<RestaurantApprovedId> {
  private final RestaurantId restaurantId;
  private final OrderId orderId;
  private final RestaurantStatus approvalStatus;

  private RestaurantApproved(Builder builder) {
    setId(builder.restaurantApprovedId);
    restaurantId = builder.restaurantId;
    orderId = builder.orderId;
    approvalStatus = builder.approvalStatus;
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

  public RestaurantStatus getApprovalStatus() {
    return approvalStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    RestaurantApproved that = (RestaurantApproved) o;
    return Objects.equals(restaurantId, that.restaurantId) && Objects.equals(orderId, that.orderId) && approvalStatus == that.approvalStatus;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), restaurantId, orderId, approvalStatus);
  }

  public static final class Builder {
    private RestaurantApprovedId restaurantApprovedId;
    private RestaurantId restaurantId;
    private OrderId orderId;
    private RestaurantStatus approvalStatus;

    private Builder() {
    }

    public Builder restaurantApprovalId(RestaurantApprovedId val) {
      restaurantApprovedId = val;
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

    public Builder approvalStatus(RestaurantStatus val) {
      approvalStatus = val;
      return this;
    }

    public RestaurantApproved build() {
      return new RestaurantApproved(this);
    }
  }
}
