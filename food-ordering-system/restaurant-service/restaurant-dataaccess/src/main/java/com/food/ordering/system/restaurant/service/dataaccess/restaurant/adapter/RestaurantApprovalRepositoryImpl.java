package com.food.ordering.system.restaurant.service.dataaccess.restaurant.adapter;

import com.food.ordering.system.restaurant.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import com.food.ordering.system.restaurant.service.dataaccess.restaurant.repository.OrderApprovalJpaRepository;
import com.food.ordering.system.restaurant.service.domain.entity.RestaurantApproval;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantApprovalRepository;
import org.springframework.stereotype.Component;

@Component
public class RestaurantApprovalRepositoryImpl implements RestaurantApprovalRepository {

  private final OrderApprovalJpaRepository orderApprovalJpaRepository;
  private final RestaurantDataAccessMapper restaurantDataAccessMapper;

  public RestaurantApprovalRepositoryImpl(
    OrderApprovalJpaRepository orderApprovalJpaRepository,
    RestaurantDataAccessMapper restaurantDataAccessMapper) {
    this.orderApprovalJpaRepository = orderApprovalJpaRepository;
    this.restaurantDataAccessMapper = restaurantDataAccessMapper;
  }

  @Override
  public void save(RestaurantApproval restaurantApproval) {
    restaurantDataAccessMapper
      .orderApprovalEntityToOrderApproval(orderApprovalJpaRepository
        .save(restaurantDataAccessMapper.orderApprovalToOrderApprovalEntity(restaurantApproval)));
  }

}
