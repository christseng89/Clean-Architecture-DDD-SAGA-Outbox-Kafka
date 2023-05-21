package com.food.ordering.system.restaurant.service.dataaccess.mapper;

import com.food.ordering.system.dataaccess.restaurant.entity.RestaurantEntity;
import com.food.ordering.system.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.dataaccess.entity.RestaurantStatusEntity;
import com.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantStatus;
import com.food.ordering.system.restaurant.service.domain.valueobject.RestaurantStatusId;
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

  public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {
    RestaurantEntity restaurantEntity =
      restaurantEntities.stream().findFirst().orElseThrow(() ->
        new RestaurantDataAccessException("No restaurants found!"));

    List<Product> restaurantProducts = restaurantEntities.stream().map(entity ->
        Product.builder()
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

  public RestaurantStatusEntity restaurantStatusToRestaurantStatusEntity(RestaurantStatus restaurantStatus) {
    return RestaurantStatusEntity.builder()
      .id(restaurantStatus.getId().getValue())
      .restaurantId(restaurantStatus.getRestaurantId().getValue())
      .orderId(restaurantStatus.getOrderId().getValue())
      .status(restaurantStatus.getStatusStatus())
      .build();
  }

  public RestaurantStatus restaurantStatusEntityToRestaurantStatus(RestaurantStatusEntity restaurantStatusEntity) {
    return RestaurantStatus.builder()
      .restaurantStatusId(new RestaurantStatusId(restaurantStatusEntity.getId()))
      .restaurantId(new RestaurantId(restaurantStatusEntity.getRestaurantId()))
      .orderId(new OrderId(restaurantStatusEntity.getOrderId()))
      .statusStatus(restaurantStatusEntity.getStatus())
      .build();
  }

}
