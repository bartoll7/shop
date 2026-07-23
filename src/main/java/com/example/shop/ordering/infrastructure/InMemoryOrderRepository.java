package com.example.shop.ordering.infrastructure;

import com.example.shop.ordering.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InMemoryOrderRepository — an ADAPTER implementing the OrderRepository port.
 *
 * <p>Lives in infrastructure. The domain has no idea this exists. Later we can add
 * a JpaOrderRepository implementing the SAME port, and the domain won't change at
 * all — that's the whole point of the port/adapter split (hexagon, weeks 9-10).
 *
 * <p>Note the defensive copy of the lines on save: we store a snapshot the caller
 * cannot later mutate behind our back. A "collection of aggregates" hands back
 * independent aggregates, not shared references into its internal store.
 */
@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<OrderId, StoredOrder> store = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        // Capture the aggregate's state as a snapshot (decoupled from the live object).
        store.put(order.id(), new StoredOrder(
            order.id(),
            order.customerId(),
            order.status(),
            List.copyOf(order.lines())   // lines are immutable VOs; copy the list
        ));
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        StoredOrder stored = store.get(id);
        if (stored == null) {
            return Optional.empty();
        }
        // Reconstitute a fresh aggregate from stored state on every read.
        return Optional.of(Order.reconstitute(
            stored.id(),
            stored.customerId(),
            stored.status(),
            stored.lines()
        ));
    }

    @Override
    public List<Order> findByCustomerId(CustomerId customerId) {
        return store.values().stream()
            .filter(stored -> stored.customerId().equals(customerId))
            .map(stored -> Order.reconstitute(
                stored.id(),
                stored.customerId(),
                stored.status(),
                stored.lines()
            ))
            .toList();
    }

    // The "persistence model": a plain snapshot of what we stored. In a JPA adapter
    // this would be a @Entity class; here it's just a record. The domain never sees it.
    private record StoredOrder(OrderId id,
                               CustomerId customerId,
                               com.example.shop.ordering.domain.OrderStatus status,
                               List<OrderLine> lines) {
    }
}