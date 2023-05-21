package com.food.ordering.system.restaurant.service.dataaccess.restaurant.adapter;

import com.food.ordering.system.restaurant.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import com.food.ordering.system.restaurant.service.dataaccess.restaurant.repository.RestaurantApprovedJpaRepository;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantApproved;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantApprovedRepository;
import org.springframework.stereotype.Component;

@Component
public class RestaurantApprovedRepositoryImpl implements RestaurantApprovedRepository {

  private final RestaurantApprovedJpaRepository restaurantApprovedJpaRepository;
  private final RestaurantDataAccessMapper restaurantDataAccessMapper;

  public RestaurantApprovedRepositoryImpl(
    RestaurantApprovedJpaRepository restaurantApprovedJpaRepository,
    RestaurantDataAccessMapper restaurantDataAccessMapper) {
    this.restaurantApprovedJpaRepository = restaurantApprovedJpaRepository;
    this.restaurantDataAccessMapper = restaurantDataAccessMapper;
  }

  @Override
  public void save(RestaurantApproved restaurantApproved) {
    restaurantDataAccessMapper
      .restaurantApprovalEntityToRestaurantApproval(restaurantApprovedJpaRepository
        .save(restaurantDataAccessMapper.restaurantApprovalToRestaurantApprovalEntity(restaurantApproved)));
  }

}
