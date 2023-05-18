package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.outbox.model.restaurant.OrderRestaurantOutboxMessage;
import com.food.ordering.system.order.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import com.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import com.food.ordering.system.saga.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.system.domain.DomainConstants.UTC;

@Slf4j
@Component
public class OrderRestaurantSaga implements SagaStep<RestaurantResponse> {

  private final OrderDomainService orderDomainService;
  private final OrderSagaHelper orderSagaHelper;
  private final PaymentOutboxHelper paymentOutboxHelper;
  private final ApprovalOutboxHelper approvalOutboxHelper;
  private final OrderDataMapper orderDataMapper;

  public OrderRestaurantSaga(
    OrderDomainService orderDomainService,
    OrderSagaHelper orderSagaHelper,
    PaymentOutboxHelper paymentOutboxHelper,
    ApprovalOutboxHelper approvalOutboxHelper,
    OrderDataMapper orderDataMapper) {
    this.orderDomainService = orderDomainService;
    this.orderSagaHelper = orderSagaHelper;
    this.paymentOutboxHelper = paymentOutboxHelper;
    this.approvalOutboxHelper = approvalOutboxHelper;
    this.orderDataMapper = orderDataMapper;
  }

  @Override
  @Transactional
  public void process(RestaurantResponse restaurantResponse) {
    Optional<OrderRestaurantOutboxMessage> orderApprovalOutboxMessageResponse =
      approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
        UUID.fromString(restaurantResponse.getSagaId()), SagaStatus.PROCESSING);

    if (orderApprovalOutboxMessageResponse.isEmpty()) {
      log.info("An outbox message with saga id: {} is already processed!",
        restaurantResponse.getSagaId());
      return;
    }

    OrderRestaurantOutboxMessage orderRestaurantOutboxMessage = orderApprovalOutboxMessageResponse.get();
    Order order = approveOrder(restaurantResponse);
    SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(
      order.getOrderStatus());

    // Update Approval Outbox
    approvalOutboxHelper
      .save(getUpdatedApprovalOutboxMessage(
        orderRestaurantOutboxMessage, order.getOrderStatus(), sagaStatus));

    // Update Payment Outbox
    paymentOutboxHelper
      .save(getUpdatedPaymentOutboxMessage(
        restaurantResponse.getSagaId(), order.getOrderStatus(), sagaStatus));

    log.info("Order with id: {} is approved", order.getId().getValue());
  }

  @Override
  @Transactional
  public void rollback(RestaurantResponse restaurantResponse) {
    Optional<OrderRestaurantOutboxMessage> orderApprovalOutboxMessageResponse =
      approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
        UUID.fromString(restaurantResponse.getSagaId()), SagaStatus.PROCESSING);

    if (orderApprovalOutboxMessageResponse.isEmpty()) {
      log.info("An outbox message with saga id: {} is already roll backed!",
        restaurantResponse.getSagaId());
      return;
    }

    OrderRestaurantOutboxMessage orderRestaurantOutboxMessage = orderApprovalOutboxMessageResponse.get();
    OrderCancelledEvent cancelledEvent = rollbackOrder(restaurantResponse);
    SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(
      cancelledEvent.getOrder().getOrderStatus());

    // Update Approval Outbox
    approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(
      orderRestaurantOutboxMessage, cancelledEvent.getOrder().getOrderStatus(), sagaStatus));

    // Payment Outbox Message (Cancelled) STARTED
    paymentOutboxHelper.savePaymentOutboxMessage(
      orderDataMapper.orderPaymentEventCancelledPayload(cancelledEvent),
      cancelledEvent.getOrder().getOrderStatus(),
      sagaStatus,
      OutboxStatus.STARTED,
      UUID.fromString(restaurantResponse.getSagaId()));

    log.info("Order with id: {} is cancelling", cancelledEvent.getOrder().getId().getValue());
  }

  private Order approveOrder(RestaurantResponse restaurantResponse) {
    log.info("Approving order with id: {}", restaurantResponse.getOrderId());
    Order order = orderSagaHelper.findOrder(restaurantResponse.getOrderId());
    orderDomainService.approveOrder(order);
    orderSagaHelper.saveOrder(order);
    return order;
  }

  private OrderRestaurantOutboxMessage getUpdatedApprovalOutboxMessage(
    OrderRestaurantOutboxMessage orderRestaurantOutboxMessage,
    OrderStatus orderStatus,
    SagaStatus sagaStatus) {
    orderRestaurantOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
    orderRestaurantOutboxMessage.setOrderStatus(orderStatus);
    orderRestaurantOutboxMessage.setSagaStatus(sagaStatus);
    return orderRestaurantOutboxMessage;
  }

  private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(
    String sagaId,
    OrderStatus orderStatus,
    SagaStatus sagaStatus) {
    Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
      paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
        UUID.fromString(sagaId), SagaStatus.PROCESSING);
    if (orderPaymentOutboxMessageResponse.isEmpty()) {
      throw new OrderDomainException("Payment outbox message cannot be found in " +
        SagaStatus.PROCESSING.name() + " state");
    }
    OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();
    orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
    orderPaymentOutboxMessage.setOrderStatus(orderStatus);
    orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
    return orderPaymentOutboxMessage;
  }

  private OrderCancelledEvent rollbackOrder(RestaurantResponse restaurantResponse) {
    log.info("Cancelling order with id: {}", restaurantResponse.getOrderId());
    Order order = orderSagaHelper.findOrder(restaurantResponse.getOrderId());
    OrderCancelledEvent cancelledEvent = orderDomainService.cancelOrderPayment(order,
      restaurantResponse.getFailureMessages());
    orderSagaHelper.saveOrder(order);
    return cancelledEvent;
  }
}
