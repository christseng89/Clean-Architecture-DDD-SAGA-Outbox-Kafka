package com.food.ordering.system.saga;

public enum SagaStatus {
  STARTED,
  PROCESSING,
  SUCCEEDED,
  COMPENSATING,
  COMPENSATED,
  FAILED,
}
