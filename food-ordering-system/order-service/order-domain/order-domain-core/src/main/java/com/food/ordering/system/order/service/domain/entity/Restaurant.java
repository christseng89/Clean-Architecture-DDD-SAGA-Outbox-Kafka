package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.RestaurantId;

import java.util.List;
import java.util.Objects;

public class Restaurant extends BaseEntity<RestaurantId> {
  private final List<Product> products;
  private final boolean active;

  private Restaurant(Builder builder) {
    super.setId(builder.restaurantId);
    products = builder.products;
    active = builder.active;
  }

  public static Builder builder() {
    return new Builder();
  }

  public List<Product> getProducts() {
    return products;
  }

  public boolean isActive() {
    return active;
  }

  public static final class Builder {
    private RestaurantId restaurantId;
    private List<Product> products;
    private boolean active;

    private Builder() {
    }

    public Builder restaurantId(RestaurantId val) {
      restaurantId = val;
      return this;
    }

    public Builder products(List<Product> val) {
      products = val;
      return this;
    }

    public Builder active(boolean val) {
      active = val;
      return this;
    }

    public Restaurant build() {
      return new Restaurant(this);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Restaurant that = (Restaurant) o;
    return active == that.active && Objects.equals(products, that.products);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), products, active);
  }
}
