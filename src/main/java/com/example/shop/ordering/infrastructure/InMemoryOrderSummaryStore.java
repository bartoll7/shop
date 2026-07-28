package com.example.shop.ordering.infrastructure;

import com.example.shop.ordering.domain.CustomerId;
import com.example.shop.ordering.domain.OrderId;
import com.example.shop.ordering.query.OrderSummary;
import com.example.shop.ordering.query.OrderSummaryQuery;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * An in-memory implementation of OrderSummaryQuery for testing and development purposes.
 * This class is a simple repository that stores OrderSummary objects in a concurrent hash map.
 */
@Repository
public class InMemoryOrderSummaryStore implements OrderSummaryQuery {
    private final Map<OrderId, OrderSummary> store = new ConcurrentHashMap<>();

    @Override
    public Optional<OrderSummary> byId(OrderId orderId) {
        return Optional.ofNullable(store.get(orderId));
    }

    @Override
    public List<OrderSummary> byCustomer(CustomerId customerId) {
        return store.values().stream()
                .filter(summary -> summary.customerId().equals(customerId))
                .sorted(Comparator.comparing(OrderSummary::placedAt).reversed()
                .thenComparing(s -> s.id().value()))
                .toList();
    }

    public void save(OrderSummary summary) {
        store.put(summary.id(), summary);
    }
}
