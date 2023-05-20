package com.food.ordering.system.payment.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.payment.service.domain.valueobject.CreditHistoryId;
import com.food.ordering.system.payment.service.domain.valueobject.PaymentType;

import java.util.Objects;

public class CreditHistory extends BaseEntity<CreditHistoryId> {

  private final CustomerId customerId;
  private final Money amount;
  private final PaymentType paymentType;

  private CreditHistory(Builder builder) {
    setId(builder.creditHistoryId);
    customerId = builder.customerId;
    amount = builder.amount;
    paymentType = builder.paymentType;
  }

  public static Builder builder() {
    return new Builder();
  }

  public CustomerId getCustomerId() {
    return customerId;
  }

  public Money getAmount() {
    return amount;
  }

  public PaymentType getTransactionType() {
    return paymentType;
  }

  public static final class Builder {
    private CreditHistoryId creditHistoryId;
    private CustomerId customerId;
    private Money amount;
    private PaymentType paymentType;

    private Builder() {
    }

    public Builder creditHistoryId(CreditHistoryId val) {
      creditHistoryId = val;
      return this;
    }

    public Builder customerId(CustomerId val) {
      customerId = val;
      return this;
    }

    public Builder amount(Money val) {
      amount = val;
      return this;
    }

    public Builder transactionType(PaymentType val) {
      paymentType = val;
      return this;
    }

    public CreditHistory build() {
      return new CreditHistory(this);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CreditHistory that = (CreditHistory) o;
    return Objects.equals(customerId, that.customerId) && Objects.equals(amount, that.amount) && paymentType == that.paymentType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), customerId, amount, paymentType);
  }
}
