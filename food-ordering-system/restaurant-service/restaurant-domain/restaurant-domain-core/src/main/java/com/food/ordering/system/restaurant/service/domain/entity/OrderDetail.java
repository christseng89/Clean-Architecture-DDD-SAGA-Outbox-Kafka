package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.outbox.OrderStatus;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;

import java.util.List;
import java.util.Objects;

public class OrderDetail extends BaseEntity<OrderId> {
  private final List<Product> products;
  private final OrderStatus orderStatus;
  private final Money totalAmount;

  private OrderDetail(Builder builder) {
    setId(builder.orderId);
    orderStatus = builder.orderStatus;
    totalAmount = builder.totalAmount;
    products = builder.products;
  }

  public static Builder builder() {
    return new Builder();
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public Money getTotalAmount() {
    return totalAmount;
  }

  public List<Product> getProducts() {
    return products;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OrderDetail that = (OrderDetail) o;
    return Objects.equals(products, that.products) && orderStatus == that.orderStatus && Objects.equals(totalAmount, that.totalAmount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), products, orderStatus, totalAmount);
  }

  public static final class Builder {
    private OrderId orderId;
    private OrderStatus orderStatus;
    private Money totalAmount;
    private List<Product> products;

    private Builder() {
    }

    public Builder orderId(OrderId val) {
      orderId = val;
      return this;
    }

    public Builder orderStatus(OrderStatus val) {
      orderStatus = val;
      return this;
    }

    public Builder totalAmount(Money val) {
      totalAmount = val;
      return this;
    }

    public Builder products(List<Product> val) {
      products = val;
      return this;
    }

    public OrderDetail build() {
      return new OrderDetail(this);
    }
  }
}
