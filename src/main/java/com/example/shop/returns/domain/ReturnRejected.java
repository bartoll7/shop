package com.example.shop.returns.domain;

import com.example.shop.shared.DomainEvent;
import java.time.Instant;

/**
 * ReturnRejected — a merchant has rejected a return. Past tense: it already happened.
 */
public record ReturnRejected(ReturnId returnId, OrderId orderId, Instant occurredOn) implements DomainEvent {

    public static ReturnRejected now(ReturnId returnId, OrderId orderId) {
        return new ReturnRejected(returnId, orderId, Instant.now());
    }
}