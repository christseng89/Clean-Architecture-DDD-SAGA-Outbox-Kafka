package com.food.ordering.system.restaurant.service.dataaccess.entity;

import com.food.ordering.system.domain.outbox.RestaurantStatus;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_status", schema = "restaurant")
@Entity
public class RestaurantStatusEntity {

  @Id
  private UUID id;
  private UUID restaurantId;
  private UUID orderId;
  @Enumerated(EnumType.STRING)
  private RestaurantStatus status;
}
