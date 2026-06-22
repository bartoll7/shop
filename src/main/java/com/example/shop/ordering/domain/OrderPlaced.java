package com.example.shop.ordering.domain;

import com.example.shop.shared.DomainEvent;

import java.time.Instant;

/**
 * OrderPlaced — a customer has placed an order. Past tense: it already happened.
 */
public record OrderPlaced(OrderId orderId, Instant occurredOn) implements DomainEvent {

    public static OrderPlaced now(OrderId orderId) {
        return new OrderPlaced(orderId, Instant.now());
    }
}