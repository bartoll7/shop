package com.example.shop.ordering.domain;

import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.Money;

import java.time.Instant;

/**
 * OrderPaid — an order has been paid. Carries the minimum a listener needs:
 * which order, the total, and when. NOT the whole aggregate.
 *
 * <p>This is the event the warehouse context will listen to (eventual consistency)
 * to release/reserve stock — without Order knowing the warehouse exists.
 */
public record OrderPaid(OrderId orderId, Money totalAmount, Instant occurredOn) implements DomainEvent {

    public static OrderPaid now(OrderId orderId, Money totalAmount) {
        return new OrderPaid(orderId, totalAmount, Instant.now());
    }
}