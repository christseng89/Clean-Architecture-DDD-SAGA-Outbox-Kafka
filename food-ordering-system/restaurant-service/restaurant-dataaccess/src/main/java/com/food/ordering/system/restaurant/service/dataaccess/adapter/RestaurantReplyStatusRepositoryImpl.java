package com.food.ordering.system.restaurant.service.dataaccess.adapter;

import com.food.ordering.system.restaurant.service.dataaccess.mapper.RestaurantDataAccessMapper;
import com.food.ordering.system.restaurant.service.dataaccess.outbox.repository.RestaurantReplyStatusJpaRepository;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantReplyStatus;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantReplyStatusRepository;
import org.springframework.stereotype.Component;

@Component
public class RestaurantReplyStatusRepositoryImpl implements RestaurantReplyStatusRepository {

  private final RestaurantReplyStatusJpaRepository restaurantReplyStatusJpaRepository;
  private final RestaurantDataAccessMapper restaurantDataAccessMapper;

  public RestaurantReplyStatusRepositoryImpl(
    RestaurantReplyStatusJpaRepository restaurantReplyStatusJpaRepository,
    RestaurantDataAccessMapper restaurantDataAccessMapper) {
    this.restaurantReplyStatusJpaRepository = restaurantReplyStatusJpaRepository;
    this.restaurantDataAccessMapper = restaurantDataAccessMapper;
  }

  @Override
  public void save(RestaurantReplyStatus restaurantReplyStatus) {
    restaurantDataAccessMapper
      .restaurantStatusEntityToRestaurantStatus(restaurantReplyStatusJpaRepository
        .save(restaurantDataAccessMapper.restaurantStatusToRestaurantStatusEntity(restaurantReplyStatus)));
  }

}
