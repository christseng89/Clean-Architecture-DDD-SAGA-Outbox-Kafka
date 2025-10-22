package com.food.ordering.system.restaurant.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.food.ordering.system.restaurant.service.domain.exception.RestaurantApplicationServiceException;
import com.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.food.ordering.system.restaurant.service.domain.ports.input.message.listener.RestaurantApprovalRequestMessageListener;
import com.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class RestaurantApprovalRequestKafkaListener implements KafkaConsumer<RestaurantApprovalRequestAvroModel> {

  private final RestaurantApprovalRequestMessageListener restaurantApprovalRequestMessageListener;
  private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;

  public RestaurantApprovalRequestKafkaListener(
    RestaurantApprovalRequestMessageListener
      restaurantApprovalRequestMessageListener,
    RestaurantMessagingDataMapper
      restaurantMessagingDataMapper) {
    this.restaurantApprovalRequestMessageListener = restaurantApprovalRequestMessageListener;
    this.restaurantMessagingDataMapper = restaurantMessagingDataMapper;
  }

  @Override
  @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
    topics = "${restaurant-service.restaurant-approval-request-topic-name}")
  @RateLimiter(name = "restaurant-service")
  @Retryable(
    retryFor = {DataAccessException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000, multiplier = 2)
  )
  public void receive(
    @Payload List<RestaurantApprovalRequestAvroModel> messages,
    @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
    @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
    @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
    
    // Prevent DoS by limiting message batch size
    if (messages.size() > 100) {
      log.warn("Received {} messages, processing only first 100 to prevent resource exhaustion", messages.size());
      messages = messages.subList(0, 100);
    }
    log.info("{} number of orders approval requests received with keys {}, partitions {} and offsets {}" +
        ", sending for restaurant approval",
      messages.size(),
      keys.toString(),
      partitions.toString(),
      offsets.toString());

    messages.forEach(restaurantApprovalRequestAvroModel -> {
      try {
        // Validate message before processing
        if (restaurantApprovalRequestAvroModel == null) {
          log.warn("Received null restaurant approval request, skipping");
          return;
        }
        
        if (restaurantApprovalRequestAvroModel.getOrderId() == null || 
            restaurantApprovalRequestAvroModel.getRestaurantId() == null) {
          log.warn("Received invalid restaurant approval request with null IDs, skipping");
          return;
        }
        
        log.info("Processing order approval for order id: {}", restaurantApprovalRequestAvroModel.getOrderId());
        restaurantApprovalRequestMessageListener.approveOrder(restaurantMessagingDataMapper.
          restaurantApprovalRequestAvroModelToRestaurantApproval(restaurantApprovalRequestAvroModel));
          
      } catch (IllegalArgumentException e) {
        log.error("Invalid input data for restaurant approval request: {}", e.getMessage());
        // Don't rethrow - this is a validation error, not a transient failure
      } catch (DataAccessException e) {
        SQLException sqlException = (SQLException) e.getRootCause();
        if (sqlException != null && sqlException.getSQLState() != null &&
          PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
          //NO-OP for unique constraint exception
          log.error("Caught unique constraint exception with sql state: {} " +
              "in RestaurantApprovalRequestKafkaListener for order id: {}",
            sqlException.getSQLState(), restaurantApprovalRequestAvroModel.getOrderId());
        } else {
          log.error("Database access error in RestaurantApprovalRequestKafkaListener: {}", e.getMessage());
          throw new RestaurantApplicationServiceException("Throwing DataAccessException in" +
            " RestaurantApprovalRequestKafkaListener: " + e.getMessage(), e);
        }
      } catch (RestaurantNotFoundException e) {
        //NO-OP for RestaurantNotFoundException
        log.error("No restaurant found for restaurant id: {}, and order id: {}",
          restaurantApprovalRequestAvroModel.getRestaurantId(),
          restaurantApprovalRequestAvroModel.getOrderId());
      } catch (Exception e) {
        log.error("Unexpected error processing restaurant approval request: {}", e.getMessage(), e);
        throw new RestaurantApplicationServiceException("Unexpected error in RestaurantApprovalRequestKafkaListener", e);
      }
    });
  }

}
