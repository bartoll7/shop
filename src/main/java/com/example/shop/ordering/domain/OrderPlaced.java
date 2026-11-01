package com.example.shop.ordering.domain;

import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.Money;
import java.time.Instant;

/**
 * OrderPlaced — a customer has placed an order. Past tense: it already happened.
 */
public record OrderPlaced(OrderId orderId, CustomerId customerId, int lineCount, Money total, Instant occurredOn) implements DomainEvent {

    public static OrderPlaced now(OrderId orderId, CustomerId customerId, int lineCount, Money total) {
        return new OrderPlaced(orderId, customerId, lineCount, total, Instant.now());
    }
}