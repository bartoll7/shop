package com.example.shop.returns.domain;

import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.Money;
import java.time.Instant;

/**
 * ReturnSent — a customer has sent a return. Past tense: it already happened.
 */
public record ReturnSent(ReturnId returnId, OrderId orderId, Money refundAmount, Instant occurredOn) implements DomainEvent {

    public static ReturnSent now(ReturnId returnId, OrderId orderId, Money refundAmount) {
        return new ReturnSent(returnId, orderId, refundAmount, Instant.now());
    }
}