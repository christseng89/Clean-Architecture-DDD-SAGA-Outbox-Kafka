package com.food.ordering.system.restaurant.service.dataaccess.adapter;

import com.food.ordering.system.restaurant.service.dataaccess.mapper.RestaurantDataAccessMapper;
import com.food.ordering.system.restaurant.service.dataaccess.outbox.repository.RestaurantRespStatusJpaRepository;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantRespStatus;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantRespStatusRepository;
import org.springframework.stereotype.Component;

@Component
public class RestaurantRespStatusRepositoryImpl implements RestaurantRespStatusRepository {

  private final RestaurantRespStatusJpaRepository restaurantRespStatusJpaRepository;
  private final RestaurantDataAccessMapper restaurantDataAccessMapper;

  public RestaurantRespStatusRepositoryImpl(
    RestaurantRespStatusJpaRepository restaurantRespStatusJpaRepository,
    RestaurantDataAccessMapper restaurantDataAccessMapper) {
    this.restaurantRespStatusJpaRepository = restaurantRespStatusJpaRepository;
    this.restaurantDataAccessMapper = restaurantDataAccessMapper;
  }

  @Override
  public void save(RestaurantRespStatus restaurantRespStatus) {
    restaurantDataAccessMapper.restaurantRespStatusEntityToRestaurantRespStatus(
      restaurantRespStatusJpaRepository.save(
        restaurantDataAccessMapper.restaurantRespStatusToRestaurantRespStatusEntity(restaurantRespStatus)));
  }

}
