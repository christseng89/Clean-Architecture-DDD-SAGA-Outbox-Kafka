package com.food.ordering.system.restaurant.service.dataaccess.restaurant.adapter;

import com.food.ordering.system.restaurant.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import com.food.ordering.system.restaurant.service.dataaccess.restaurant.repository.RestaurantOrderStatusJpaRepository;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantOrderStatus;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantOrderStatusRepository;
import org.springframework.stereotype.Component;

@Component
public class RestaurantOrderStatusRepositoryImpl implements RestaurantOrderStatusRepository {

  private final RestaurantOrderStatusJpaRepository restaurantOrderStatusJpaRepository;
  private final RestaurantDataAccessMapper restaurantDataAccessMapper;

  public RestaurantOrderStatusRepositoryImpl(
    RestaurantOrderStatusJpaRepository restaurantOrderStatusJpaRepository,
    RestaurantDataAccessMapper restaurantDataAccessMapper) {
    this.restaurantOrderStatusJpaRepository = restaurantOrderStatusJpaRepository;
    this.restaurantDataAccessMapper = restaurantDataAccessMapper;
  }

  @Override
  public void save(RestaurantOrderStatus restaurantOrderStatus) {
    restaurantDataAccessMapper.restaurantRespStatusEntityToRestaurantRespStatus(
      restaurantOrderStatusJpaRepository.save(
        restaurantDataAccessMapper.restaurantRespStatusToRestaurantRespStatusEntity(restaurantOrderStatus)));
  }

}
