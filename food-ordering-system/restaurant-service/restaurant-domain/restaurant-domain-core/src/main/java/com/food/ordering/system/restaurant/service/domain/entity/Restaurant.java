package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.outbox.OrderStatus;
import com.food.ordering.system.domain.outbox.RestaurantStatus;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.valueobject.RestaurantOrderStatusId;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Restaurant extends AggregateRoot<RestaurantId> {
  private final OrderDetail orderDetail;
  private RestaurantOrderStatus restaurantOrderStatus;
  private boolean active;

  private Restaurant(Builder builder) {
    setId(builder.restaurantId);
    restaurantOrderStatus = builder.restaurantOrderStatus;
    active = builder.active;
    orderDetail = builder.orderDetail;
  }

  public static Builder builder() {
    return new Builder();
  }

  public void validateOrder(List<String> failureMessages) {
    if (orderDetail.getOrderStatus() != OrderStatus.PAID) {
      failureMessages.add("Payment is not completed for order: " + orderDetail.getId());
    }
    Money totalAmount = orderDetail.getProducts().stream()
      .map(product -> {
        if (!product.isAvailable()) {
          failureMessages.add("Product with id: " + product.getId().getValue()
            + " is not available");
        }
        return product.getPrice().multiply(product.getQuantity());
      }).reduce(Money.ZERO, Money::add);

    if (!totalAmount.equals(orderDetail.getTotalAmount())) {
      failureMessages.add("Price total is not correct for order: " + orderDetail.getId());
    }
  }

  public void createRestaurantStatus(RestaurantStatus restaurantStatus) {
    this.restaurantOrderStatus = RestaurantOrderStatus.builder()
      .restaurantRespStatusId(new RestaurantOrderStatusId(UUID.randomUUID()))
      .restaurantId(this.getId())
      .orderId(this.getOrderDetail().getId())
      .restaurantStatus(restaurantStatus)
      .build();
  }

  public RestaurantOrderStatus getRestaurantStatus() {
    return restaurantOrderStatus;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public OrderDetail getOrderDetail() {
    return orderDetail;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Restaurant that = (Restaurant) o;
    return active == that.active && Objects.equals(orderDetail, that.orderDetail) && Objects.equals(restaurantOrderStatus, that.restaurantOrderStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), orderDetail, restaurantOrderStatus, active);
  }

  public static final class Builder {
    private RestaurantId restaurantId;
    private RestaurantOrderStatus restaurantOrderStatus;
    private boolean active;
    private OrderDetail orderDetail;

    private Builder() {
    }

    public Builder restaurantId(RestaurantId val) {
      restaurantId = val;
      return this;
    }

    public Builder restaurantStatus(RestaurantOrderStatus val) {
      restaurantOrderStatus = val;
      return this;
    }

    public Builder active(boolean val) {
      active = val;
      return this;
    }

    public Builder orderDetail(OrderDetail val) {
      orderDetail = val;
      return this;
    }

    public Restaurant build() {
      return new Restaurant(this);
    }
  }
}
