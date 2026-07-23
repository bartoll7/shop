package com.example.shop.returns.domain;

import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.Money;
import java.time.Instant;

/**
 * ReturnAccepted — a merchant has accepted a return. Past tense: it already happened.
 */
public record ReturnAccepted(ReturnId returnId, OrderId orderId, Money refundAmount, Instant occurredOn) implements DomainEvent {

    public static ReturnAccepted now(ReturnId returnId, OrderId orderId, Money refundAmount) {
        return new ReturnAccepted(returnId, orderId, refundAmount, Instant.now());
    }
}