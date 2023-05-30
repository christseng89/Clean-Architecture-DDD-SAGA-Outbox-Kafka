package com.food.ordering.system.restaurant.service.dataaccess.outbox.repository;

import com.food.ordering.system.restaurant.service.dataaccess.entity.RestaurantRespStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RestaurantRespStatusJpaRepository extends JpaRepository<RestaurantRespStatusEntity, UUID> {

}
