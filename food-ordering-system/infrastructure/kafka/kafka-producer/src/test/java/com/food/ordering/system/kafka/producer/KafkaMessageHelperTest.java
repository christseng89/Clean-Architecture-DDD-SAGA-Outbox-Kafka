package com.food.ordering.system.kafka.producer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.SendResult;

import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaMessageHelperTest {

  @InjectMocks
  private KafkaMessageHelper kafkaMessageHelper;

  @Test
  public void testGetKafkaCallbackSuccess() {
    String responseTopicName = "test-topic";
    Object avroModel = new Object();
    String orderId = "test-order-id";

    BiConsumer<SendResult<String, Object>, Throwable> callback =
      kafkaMessageHelper.getKafkaCallback(responseTopicName, avroModel, orderId, avroModel.getClass().getName());

    assertNotNull(callback);

    // Test success path
    SendResult<String, Object> sendResult = mock(SendResult.class);
    RecordMetadata recordMetadata = mock(RecordMetadata.class);
    when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
    when(recordMetadata.partition()).thenReturn(0);
    when(recordMetadata.offset()).thenReturn(0L);
    when(recordMetadata.timestamp()).thenReturn(System.currentTimeMillis());

    assertDoesNotThrow(() -> callback.accept(sendResult, null));
  }

  @Test
  public void testGetKafkaCallbackFailure() {
    String responseTopicName = "test-topic";
    Object avroModel = new Object();
    String orderId = "test-order-id";

    BiConsumer<SendResult<String, Object>, Throwable> callback =
      kafkaMessageHelper.getKafkaCallback(responseTopicName, avroModel, orderId, avroModel.getClass().getName());

    assertNotNull(callback);

    // Test failure path
    Throwable exception = new RuntimeException("Kafka send failed");
    assertDoesNotThrow(() -> callback.accept(null, exception));
  }

  @Test
  public void testGetKafkaCallbackWithNullValues() {
    String responseTopicName = null;
    Object avroModel = null;
    String orderId = null;
    String avroModelName = null;

    BiConsumer<SendResult<String, Object>, Throwable> callback =
      kafkaMessageHelper.getKafkaCallback(responseTopicName, avroModel, orderId, avroModelName);

    assertNotNull(callback);
  }
}
