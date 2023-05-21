package com.food.ordering.system.restaurant.service.dataaccess.adapter;

import com.food.ordering.system.restaurant.service.dataaccess.mapper.RestaurantDataAccessMapper;
import com.food.ordering.system.restaurant.service.dataaccess.repository.RestaurantStatusJpaRepository;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantStatus;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantStatusRepository;
import org.springframework.stereotype.Component;

@Component
public class RestaurantStatusRepositoryImpl implements RestaurantStatusRepository {

  private final RestaurantStatusJpaRepository restaurantStatusJpaRepository;
  private final RestaurantDataAccessMapper restaurantDataAccessMapper;

  public RestaurantStatusRepositoryImpl(
    RestaurantStatusJpaRepository restaurantStatusJpaRepository,
    RestaurantDataAccessMapper restaurantDataAccessMapper) {
    this.restaurantStatusJpaRepository = restaurantStatusJpaRepository;
    this.restaurantDataAccessMapper = restaurantDataAccessMapper;
  }

  @Override
  public void save(RestaurantStatus restaurantStatus) {
    restaurantDataAccessMapper
      .restaurantStatusEntityToRestaurantStatus(restaurantStatusJpaRepository
        .save(restaurantDataAccessMapper.restaurantStatusToRestaurantStatusEntity(restaurantStatus)));
  }

}
