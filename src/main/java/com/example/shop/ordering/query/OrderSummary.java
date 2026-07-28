package com.example.shop.ordering.query;

import com.example.shop.ordering.domain.CustomerId;
import com.example.shop.ordering.domain.OrderId;
import com.example.shop.ordering.domain.OrderStatus;
import com.example.shop.shared.Money;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

/**
 * A read model representing a summary of an order. This is a SEPARATE model with its own store,
 * queried without ever touching the Order aggregate or OrderRepository.
 */
public record OrderSummary(OrderId id, CustomerId customerId, OrderStatus status, int lineCount, @Nullable Money total, Instant placedAt) {
}
