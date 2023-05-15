package com.food.ordering.system.payment.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.payment.service.domain.valueobject.CreditHistoryId;
import com.food.ordering.system.payment.service.domain.valueobject.TransactionType;

import java.util.Objects;

public class CreditHistory extends BaseEntity<CreditHistoryId> {

  private final CustomerId customerId;
  private final Money amount;
  private final TransactionType transactionType;

  private CreditHistory(Builder builder) {
    setId(builder.creditHistoryId);
    customerId = builder.customerId;
    amount = builder.amount;
    transactionType = builder.transactionType;
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

  public TransactionType getTransactionType() {
    return transactionType;
  }

  public static final class Builder {
    private CreditHistoryId creditHistoryId;
    private CustomerId customerId;
    private Money amount;
    private TransactionType transactionType;

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

    public Builder transactionType(TransactionType val) {
      transactionType = val;
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
    return Objects.equals(customerId, that.customerId) && Objects.equals(amount, that.amount) && transactionType == that.transactionType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), customerId, amount, transactionType);
  }
}
