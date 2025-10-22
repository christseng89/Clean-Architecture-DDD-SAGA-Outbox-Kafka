package com.food.ordering.system.restaurant.service.dataaccess.adapter;

import com.food.ordering.system.dataaccess.entity.RestaurantEntity;
import com.food.ordering.system.dataaccess.repository.RestaurantJpaRepository;
import com.food.ordering.system.restaurant.service.dataaccess.mapper.RestaurantDataAccessMapper;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {

  private final RestaurantJpaRepository restaurantJpaRepository;
  private final RestaurantDataAccessMapper restaurantDataAccessMapper;

  public RestaurantRepositoryImpl(
    RestaurantJpaRepository restaurantJpaRepository,
    RestaurantDataAccessMapper restaurantDataAccessMapper) {
    this.restaurantJpaRepository = restaurantJpaRepository;
    this.restaurantDataAccessMapper = restaurantDataAccessMapper;
  }

  @Override
  @Transactional(readOnly = true)
  @Retryable(
    retryFor = {DataAccessException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 100, multiplier = 2)
  )
  public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
    // Input validation to prevent malicious queries
    if (restaurant == null) {
      log.warn("Attempted to find restaurant information with null restaurant");
      throw new IllegalArgumentException("Restaurant cannot be null");
    }
    
    if (restaurant.getId() == null || restaurant.getId().getValue() == null) {
      log.warn("Attempted to find restaurant information with null restaurant ID");
      throw new IllegalArgumentException("Restaurant ID cannot be null");
    }
    
    if (restaurant.getOrderDetail() == null || restaurant.getOrderDetail().getProducts() == null) {
      log.warn("Attempted to find restaurant information with null products");
      throw new IllegalArgumentException("Restaurant products cannot be null");
    }
    
    // Prevent resource exhaustion by limiting product count
    if (restaurant.getOrderDetail().getProducts().size() > 100) {
      log.warn("Attempted to query restaurant with {} products, limiting to 100", 
        restaurant.getOrderDetail().getProducts().size());
      throw new IllegalArgumentException("Too many products in query. Maximum allowed: 100");
    }
    
    try {
      List<UUID> restaurantProducts = restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant);
      
      // Additional validation on extracted product IDs
      if (restaurantProducts.size() > 100) {
        log.warn("Extracted product IDs exceed limit: {}", restaurantProducts.size());
        restaurantProducts = restaurantProducts.subList(0, 100);
      }
      
      Optional<List<RestaurantEntity>> restaurantEntities = restaurantJpaRepository
        .findByRestaurantIdAndProductIdIn(restaurant.getId().getValue(), restaurantProducts);
        
      Optional<Restaurant> result = restaurantEntities.map(restaurantDataAccessMapper::restaurantEntityToRestaurant);
      
      log.debug("Found restaurant information for restaurant ID: {}", restaurant.getId().getValue());
      return result;
      
    } catch (DataAccessException e) {
      log.error("Database error while finding restaurant information for ID: {}", 
        restaurant.getId().getValue(), e);
      throw e;
    } catch (Exception e) {
      log.error("Unexpected error while finding restaurant information for ID: {}", 
        restaurant.getId().getValue(), e);
      throw new RuntimeException("Failed to find restaurant information", e);
    }
  }
}
