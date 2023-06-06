package com.food.ordering.system.restaurant.service.dataaccess.restaurant.mapper;

import com.food.ordering.system.dataaccess.restaurant.entity.RestaurantEntity;
import com.food.ordering.system.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.dataaccess.restaurant.entity.RestaurantOrderStatusEntity;
import com.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantOrderStatus;
import com.food.ordering.system.restaurant.service.domain.valueobject.RestaurantOrderStatusId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RestaurantDataAccessMapper {

  public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
    return restaurant.getOrderDetail().getProducts().stream()
      .map(product -> product.getId().getValue())
      .toList();
  }

  public Restaurant restaurantEntityToRestaurant(
    List<RestaurantEntity> restaurantEntities) {
    RestaurantEntity restaurantEntity =
      restaurantEntities.stream().findFirst().orElseThrow(() ->
        new RestaurantDataAccessException("No restaurants found!"));

    List<Product> restaurantProducts = restaurantEntities.stream()
      .map(entity -> Product.builder()
        .productId(new ProductId(entity.getProductId()))
        .name(entity.getProductName())
        .price(new Money(entity.getProductPrice()))
        .available(entity.getProductAvailable())
        .build())
      .toList();

    return Restaurant.builder()
      .restaurantId(new RestaurantId(restaurantEntity.getRestaurantId()))
      .orderDetail(OrderDetail.builder()
        .products(restaurantProducts)
        .build())
      .active(restaurantEntity.getRestaurantActive())
      .build();
  }

  public RestaurantOrderStatusEntity restaurantRespStatusToRestaurantRespStatusEntity(
    RestaurantOrderStatus restaurantOrderStatus) {
    return RestaurantOrderStatusEntity.builder()
      .id(restaurantOrderStatus.getId().getValue())
      .restaurantId(restaurantOrderStatus.getRestaurantId().getValue())
      .orderId(restaurantOrderStatus.getOrderId().getValue())
      .status(restaurantOrderStatus.getRestaurantStatus())
      .build();
  }

  public void restaurantRespStatusEntityToRestaurantRespStatus(
    RestaurantOrderStatusEntity restaurantOrderStatusEntity) {
    RestaurantOrderStatus.builder()
      .restaurantRespStatusId(new RestaurantOrderStatusId(restaurantOrderStatusEntity.getId()))
      .restaurantId(new RestaurantId(restaurantOrderStatusEntity.getRestaurantId()))
      .orderId(new OrderId(restaurantOrderStatusEntity.getOrderId()))
      .restaurantStatus(restaurantOrderStatusEntity.getStatus())
      .build();
  }
}
