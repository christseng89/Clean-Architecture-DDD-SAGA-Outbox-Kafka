package com.food.ordering.system.restaurant.service.dataaccess.restaurant.adapter;

import com.food.ordering.system.restaurant.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import com.food.ordering.system.restaurant.service.dataaccess.restaurant.repository.RestaurantApprovalJpaRepository;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantApproval;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantApprovalRepository;
import org.springframework.stereotype.Component;

@Component
public class RestaurantApprovalRepositoryImpl implements RestaurantApprovalRepository {

  private final RestaurantApprovalJpaRepository restaurantApprovalJpaRepository;
  private final RestaurantDataAccessMapper restaurantDataAccessMapper;

  public RestaurantApprovalRepositoryImpl(
    RestaurantApprovalJpaRepository restaurantApprovalJpaRepository,
    RestaurantDataAccessMapper restaurantDataAccessMapper) {
    this.restaurantApprovalJpaRepository = restaurantApprovalJpaRepository;
    this.restaurantDataAccessMapper = restaurantDataAccessMapper;
  }

  @Override
  public void save(RestaurantApproval restaurantApproval) {
    restaurantDataAccessMapper
      .orderApprovalEntityToOrderApproval(restaurantApprovalJpaRepository
        .save(restaurantDataAccessMapper.orderApprovalToOrderApprovalEntity(restaurantApproval)));
  }

}
