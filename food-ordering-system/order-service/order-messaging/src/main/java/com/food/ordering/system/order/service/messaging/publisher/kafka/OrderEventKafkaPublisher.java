package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.RestaurantRequestAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurant.RestaurantRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class OrderEventKafkaPublisher implements RestaurantRequestMessagePublisher {

  private final OrderMessagingDataMapper orderMessagingDataMapper;
  private final KafkaProducer<String, RestaurantRequestAvroModel> kafkaProducer;
  private final OrderServiceConfigData orderServiceConfigData;
  private final KafkaMessageHelper kafkaMessageHelper;

  public OrderEventKafkaPublisher(
    OrderMessagingDataMapper orderMessagingDataMapper,
    KafkaProducer<String, RestaurantRequestAvroModel> kafkaProducer,
    OrderServiceConfigData orderServiceConfigData,
    KafkaMessageHelper kafkaMessageHelper) {
    this.orderMessagingDataMapper = orderMessagingDataMapper;
    this.kafkaProducer = kafkaProducer;
    this.orderServiceConfigData = orderServiceConfigData;
    this.kafkaMessageHelper = kafkaMessageHelper;
  }

  @Override
  public void publish(
    // Order Restaurant Status Outbox
    OrderRestaurantOutboxMessage orderRestaurantOutboxMessage,
    BiConsumer<OrderRestaurantOutboxMessage, OutboxStatus> outboxCallback) {
    OrderRestaurantEventPayload orderRestaurantEventPayload =
      kafkaMessageHelper.getOrderEventPayload(orderRestaurantOutboxMessage.getPayload(),
        OrderRestaurantEventPayload.class);

    String sagaId = orderRestaurantOutboxMessage.getSagaId().toString();

    log.info("Received RestaurantStatusOutboxMessage for order id: {} and saga id: {}",
      orderRestaurantEventPayload.getOrderId(), sagaId);

    try {
      RestaurantRequestAvroModel restaurantRequestAvroModel =
        orderMessagingDataMapper
          .restaurantStatusRequestAvroModel(sagaId,
            orderRestaurantEventPayload);

      // Status Request
      String topicName =
        orderServiceConfigData.getRestaurantStatusRequestTopicName();

      kafkaProducer.send(
        topicName,
        sagaId,
        restaurantRequestAvroModel,
        kafkaMessageHelper.getKafkaCallback(
          topicName,
          restaurantRequestAvroModel,
          orderRestaurantOutboxMessage,
          outboxCallback,
          orderRestaurantEventPayload.getOrderId(),
          "RestaurantRequestAvroModel"));

      log.info("RestaurantApprovedEventPayload sent to Kafka for order id: {} and saga id: {}",
        restaurantRequestAvroModel.getOrderId(), sagaId);
    } catch (Exception e) {
      log.error("Error while sending RestaurantApprovedEventPayload to kafka for order id: {} and saga id: {}," +
        " error: {}", orderRestaurantEventPayload.getOrderId(), sagaId, e.getMessage());
    }
  }
}
