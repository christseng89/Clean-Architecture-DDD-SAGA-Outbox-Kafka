package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.outbox.RestaurantStatus;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.valueobject.RestaurantApprovalId;

import java.util.Objects;

public class RestaurantApproval extends BaseEntity<RestaurantApprovalId> {
  private final RestaurantId restaurantId;
  private final OrderId orderId;
  private final RestaurantStatus approvalStatus;

  private RestaurantApproval(Builder builder) {
    setId(builder.restaurantApprovalId);
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
    RestaurantApproval that = (RestaurantApproval) o;
    return Objects.equals(restaurantId, that.restaurantId) && Objects.equals(orderId, that.orderId) && approvalStatus == that.approvalStatus;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), restaurantId, orderId, approvalStatus);
  }

  public static final class Builder {
    private RestaurantApprovalId restaurantApprovalId;
    private RestaurantId restaurantId;
    private OrderId orderId;
    private RestaurantStatus approvalStatus;

    private Builder() {
    }

    public Builder restaurantApprovalId(RestaurantApprovalId val) {
      restaurantApprovalId = val;
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

    public RestaurantApproval build() {
      return new RestaurantApproval(this);
    }
  }
}
