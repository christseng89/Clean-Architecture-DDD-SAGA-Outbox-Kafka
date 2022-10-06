package com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval;

import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;

public interface OrderPaidRestaurantRequestMessagePublisher {
    void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse);

    void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse);
}
