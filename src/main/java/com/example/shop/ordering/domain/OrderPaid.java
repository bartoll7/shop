package com.example.shop.ordering.domain;

import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.Money;

import java.time.Instant;

/**
 * OrderPaid — an order has been paid. Carries the minimum a listener needs:
 * which order, which customer, the total, and when. NOT the whole aggregate.
 *
 * <p>This is the event the warehouse context will listen to (eventual consistency)
 * to release/reserve stock — without Order knowing the warehouse exists.
 */
public record OrderPaid(OrderId orderId, CustomerId customerId, Money totalAmount, Instant occurredOn) implements DomainEvent {

    public static OrderPaid now(OrderId orderId, CustomerId customerId, Money totalAmount) {
        return new OrderPaid(orderId, customerId, totalAmount, Instant.now());
    }
}