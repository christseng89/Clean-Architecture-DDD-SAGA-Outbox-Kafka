package com.food.ordering.system.order.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.RestaurantResponseAvroModel;
import com.food.ordering.system.kafka.order.avro.model.RestaurantStatus;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantResponse;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurant.RestaurantResponseMessageListener;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Slf4j
@Component
public class RestaurantResponseKafkaListener implements KafkaConsumer<RestaurantResponseAvroModel> {

  private final RestaurantResponseMessageListener restaurantResponseMessageListener;
  private final OrderMessagingDataMapper orderMessagingDataMapper;

  public RestaurantResponseKafkaListener(
    RestaurantResponseMessageListener restaurantResponseMessageListener,
    OrderMessagingDataMapper orderMessagingDataMapper) {
    this.restaurantResponseMessageListener = restaurantResponseMessageListener;
    this.orderMessagingDataMapper = orderMessagingDataMapper;
  }

  @Override
  @KafkaListener(id = "${kafka-consumer-config.restaurant-approved-consumer-group-id}",
    topics = "${order-service.restaurant-approved-response-topic-name}")
  public void receive(
    @Payload List<RestaurantResponseAvroModel> messages,
    @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
    @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
    @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
    log.info("{} number of restaurant approved responses received with keys {}, partitions {} and offsets {}",
      messages.size(), keys.toString(), partitions.toString(), offsets.toString());

    messages.forEach(restaurantApprovedResponseAvroModel -> {
      try {
        RestaurantResponse restaurantResponse = orderMessagingDataMapper
          .restaurantResponse(restaurantApprovedResponseAvroModel);

        if (RestaurantStatus.APPROVED == restaurantApprovedResponseAvroModel.getRestaurantStatus()) {
          log.info("Processing approved order for order id: {}",
            restaurantApprovedResponseAvroModel.getOrderId());
          restaurantResponseMessageListener.orderApproved(restaurantResponse);
        } else if (RestaurantStatus.REJECTED == restaurantApprovedResponseAvroModel.getRestaurantStatus()) {
          log.info("Processing rejected order for order id: {}, with failure messages: {}",
            restaurantApprovedResponseAvroModel.getOrderId(),
            String.join(FAILURE_MESSAGE_DELIMITER,
              restaurantApprovedResponseAvroModel.getFailureMessages()));
          restaurantResponseMessageListener.orderRejected(restaurantResponse);
        }
      } catch (OptimisticLockingFailureException e) {
        // NO-OP for optimistic lock. This means another thread finished the
        // work, do not throw error to prevent reading the data from kafka again!
        log.error("Caught optimistic locking exception in RestaurantApprovedResponseKafkaListener for order id: {}",
          restaurantApprovedResponseAvroModel.getOrderId());
      } catch (OrderDomainException e) {
        // NO-OP for OrderDomainException
        log.error("No order found for order id: {}", restaurantApprovedResponseAvroModel.getOrderId());
      }
    });

  }
}
