package com.food.ordering.system.restaurant.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.RestaurantResponseAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.restaurant.service.domain.config.RestaurantServiceConfigData;
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload;
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.RestaurantResponseMessagePublisher;
import com.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class RestaurantEventKafkaPublisher implements RestaurantResponseMessagePublisher {

  private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;
  private final KafkaProducer<String, RestaurantResponseAvroModel> kafkaProducer;
  private final RestaurantServiceConfigData restaurantServiceConfigData;
  private final KafkaMessageHelper kafkaMessageHelper;

  public RestaurantEventKafkaPublisher(
    RestaurantMessagingDataMapper dataMapper,
    KafkaProducer<String, RestaurantResponseAvroModel> kafkaProducer,
    RestaurantServiceConfigData restaurantServiceConfigData,
    KafkaMessageHelper kafkaMessageHelper) {
    this.restaurantMessagingDataMapper = dataMapper;
    this.kafkaProducer = kafkaProducer;
    this.restaurantServiceConfigData = restaurantServiceConfigData;
    this.kafkaMessageHelper = kafkaMessageHelper;
  }

  @Override
  public void publish(
    // Restaurant Order Outbox
    OrderOutboxMessage orderOutboxMessage,
    BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {
    OrderEventPayload orderEventPayload =
      kafkaMessageHelper.getOrderEventPayload(orderOutboxMessage.getPayload(),
        OrderEventPayload.class);

    String sagaId = orderOutboxMessage.getSagaId().toString();

    log.info("Received OrderOutboxMessage for order id: {} and saga id: {}",
      orderEventPayload.getOrderId(), sagaId);
    try {
      RestaurantResponseAvroModel restaurantResponseAvroModel =
        restaurantMessagingDataMapper
          .orderEventPayloadToRestaurantResponseAvroModel(sagaId, orderEventPayload);

      // Approved Response
      String topicName = restaurantServiceConfigData.getRestaurantApprovedResponseTopicName();

      kafkaProducer.send(
        topicName,
        sagaId,
        restaurantResponseAvroModel,
        kafkaMessageHelper.getKafkaCallback(
          topicName,
          restaurantResponseAvroModel,
          orderOutboxMessage,
          outboxCallback,
          orderEventPayload.getOrderId(),
          "RestaurantResponseAvroModel"));

      log.info("RestaurantResponseAvroModel sent to Kafka for order id: {} and saga id: {}",
        restaurantResponseAvroModel.getOrderId(), sagaId);
    } catch (Exception e) {
      log.error("Error while sending RestaurantResponseAvroModel message" +
          " to kafka with order id: {} and saga id: {}, error: {}",
        orderEventPayload.getOrderId(), sagaId, e.getMessage());
    }
  }

}
