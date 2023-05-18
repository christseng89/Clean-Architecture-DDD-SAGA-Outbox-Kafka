package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class OrderApprovalEventKafkaPublisher implements RestaurantApprovalRequestMessagePublisher {

  private final OrderMessagingDataMapper orderMessagingDataMapper;
  private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
  private final OrderServiceConfigData orderServiceConfigData;
  private final KafkaMessageHelper kafkaMessageHelper;

  public OrderApprovalEventKafkaPublisher(
    OrderMessagingDataMapper orderMessagingDataMapper,
    KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer,
    OrderServiceConfigData orderServiceConfigData,
    KafkaMessageHelper kafkaMessageHelper) {
    this.orderMessagingDataMapper = orderMessagingDataMapper;
    this.kafkaProducer = kafkaProducer;
    this.orderServiceConfigData = orderServiceConfigData;
    this.kafkaMessageHelper = kafkaMessageHelper;
  }

  @Override
  public void publish(
    // Order Restaurant Approval Outbox
    OrderRestaurantOutboxMessage orderRestaurantOutboxMessage,
    BiConsumer<OrderRestaurantOutboxMessage, OutboxStatus> outboxCallback) {
    OrderRestaurantEventPayload orderRestaurantEventPayload =
      kafkaMessageHelper.getOrderEventPayload(orderRestaurantOutboxMessage.getPayload(),
        OrderRestaurantEventPayload.class);

    String sagaId = orderRestaurantOutboxMessage.getSagaId().toString();

    log.info("Received OrderApprovalOutboxMessage for order id: {} and saga id: {}",
      orderRestaurantEventPayload.getOrderId(), sagaId);

    try {
      RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel =
        orderMessagingDataMapper
          .orderApprovalEventToRestaurantApprovalRequestAvroModel(sagaId,
            orderRestaurantEventPayload);

      // Approval Request
      String topicName =
        orderServiceConfigData.getRestaurantApprovalRequestTopicName();

      kafkaProducer.send(
        topicName,
        sagaId,
        restaurantApprovalRequestAvroModel,
        kafkaMessageHelper.getKafkaCallback(
          topicName,
          restaurantApprovalRequestAvroModel,
          orderRestaurantOutboxMessage,
          outboxCallback,
          orderRestaurantEventPayload.getOrderId(),
          "RestaurantApprovalRequestAvroModel"));

      log.info("OrderApprovalEventPayload sent to Kafka for order id: {} and saga id: {}",
        restaurantApprovalRequestAvroModel.getOrderId(), sagaId);
    } catch (Exception e) {
      log.error("Error while sending OrderApprovalEventPayload to kafka for order id: {} and saga id: {}," +
        " error: {}", orderRestaurantEventPayload.getOrderId(), sagaId, e.getMessage());
    }
  }
}
