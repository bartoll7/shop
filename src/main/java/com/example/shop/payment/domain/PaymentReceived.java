package com.example.shop.payment.domain;

import com.example.shop.ordering.domain.OrderId;
import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.Money;

import java.time.Instant;

/**
 * PaymentReceived — OUR domain event: a payment for an order has succeeded.
 *
 * <p>This is the clean, internal fact that the ordering context listens to. The
 * external provider's vocabulary never reaches this far — the ACL stops it.
 * Carries the order reference so ordering can mark the right order as paid.
 */
public record PaymentReceived(PaymentId paymentId, OrderId orderId, Money amount, Instant occurredOn)
    implements DomainEvent {

    public static PaymentReceived now(PaymentId paymentId, OrderId orderId, Money amount) {
        return new PaymentReceived(paymentId, orderId, amount, Instant.now());
    }
}