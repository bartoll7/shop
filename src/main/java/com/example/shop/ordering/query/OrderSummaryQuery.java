package com.example.shop.ordering.query;

import com.example.shop.ordering.domain.CustomerId;
import com.example.shop.ordering.domain.OrderId;
import java.util.List;
import java.util.Optional;

/**
 * A query interface for retrieving OrderSummary read models. This is a SEPARATE model with its own store,
 * queried without ever touching the Order aggregate or OrderRepository.
 */
public interface OrderSummaryQuery {
    /**
     * Retrieves an OrderSummary object by its order ID. Returns an empty Optional if the order summary is not found.
     */
    Optional<OrderSummary> byId(OrderId orderId);
    /**
     * Retrieves a list of OrderSummary objects for a given customer, sorted by the placedAt timestamp in descending order.
     * Returns an empty list if the customer has no order summaries.
     */
    List<OrderSummary> byCustomer(CustomerId customerId);
}
