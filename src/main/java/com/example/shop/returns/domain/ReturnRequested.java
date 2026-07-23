package com.example.shop.returns.domain;

import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.Money;
import java.time.Instant;

/**
 * ReturnRequested — a customer has placed a return. Past tense: it already happened.
 */
public record ReturnRequested(ReturnId returnId, OrderId orderId, Money refundAmount, Instant occurredOn) implements DomainEvent {

    public static ReturnRequested now(ReturnId returnId, OrderId orderId, Money refundAmount) {
        return new ReturnRequested(returnId, orderId, refundAmount, Instant.now());
    }
}