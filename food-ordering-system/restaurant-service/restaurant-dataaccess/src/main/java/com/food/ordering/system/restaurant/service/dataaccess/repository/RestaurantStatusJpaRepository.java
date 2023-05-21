package com.food.ordering.system.restaurant.service.dataaccess.repository;

import com.food.ordering.system.restaurant.service.dataaccess.entity.RestaurantStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RestaurantStatusJpaRepository extends JpaRepository<RestaurantStatusEntity, UUID> {

}
