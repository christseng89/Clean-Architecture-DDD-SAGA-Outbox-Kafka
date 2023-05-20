package com.food.ordering.system.domain.outbox;

public enum OrderStatus {
  PENDING,
  PAID,
  APPROVED,
  CANCELLING,
  CANCELLED
}
