package com.food.ordering.system.restaurant.service.dataaccess.restaurant.repository;

import com.food.ordering.system.restaurant.service.dataaccess.restaurant.entity.RestaurantRespStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RestaurantRespStatusJpaRepository extends JpaRepository<RestaurantRespStatusEntity, UUID> {

}
