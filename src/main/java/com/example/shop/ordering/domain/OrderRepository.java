package com.example.shop.ordering.domain;

import java.util.List;
import java.util.Optional;

/**
 * OrderRepository — a PORT, owned by the domain.
 *
 * <p>This is NOT a DAO. It models a *collection of Order aggregates*: you save a
 * whole aggregate and you retrieve a whole aggregate (with its lines). The domain
 * declares WHAT it needs to persist; it does not know HOW. Implementations live in
 * the infrastructure layer, so the dependency arrow points inward, toward the domain.
 *
 * <p>One aggregate, one repository. There is deliberately no OrderLineRepository —
 * lines are part of the Order aggregate and travel with it.
 */
public interface OrderRepository {

    /** Add a new aggregate or replace an existing one (by identity). */
    void save(Order order);

    /** Retrieve a whole aggregate by its identity, if present. */
    Optional<Order> findById(OrderId id);

    /** Retrieve all orders for a given customer. */
    List<Order> findByCustomerId(CustomerId customerId);
}