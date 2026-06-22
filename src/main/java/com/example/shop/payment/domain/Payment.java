package com.example.shop.payment.domain;

import com.example.shop.ordering.domain.OrderId;
import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.Money;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Payment — Aggregate Root of the payment context.
 *
 * <p>Tracks one payment attempt for one order through its lifecycle. Same DDD rules
 * as Order: identity-based equality, controlled state transitions guarding invariants,
 * domain events collected internally and pulled after persistence.
 */
public class Payment {

    private final PaymentId id;
    private final OrderId orderId;
    private final Money amount;
    private PaymentStatus status;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Payment(PaymentId id, OrderId orderId, Money amount, PaymentStatus status) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    /** Initiate a new payment for an order. Starts as INITIATED. */
    public static Payment initiate(OrderId orderId, Money amount) {
        return new Payment(PaymentId.newId(), orderId, amount, PaymentStatus.INITIATED);
    }

    /** Reconstitute from storage — no events emitted. */
    public static Payment reconstitute(PaymentId id, OrderId orderId, Money amount, PaymentStatus status) {
        return new Payment(id, orderId, amount, status);
    }

    /** The provider confirmed success. Records PaymentReceived. */
    public void markSucceeded() {
        if (status != PaymentStatus.INITIATED) {
            throw new IllegalStateException("Only an INITIATED payment can succeed (was: " + status + ")");
        }
        this.status = PaymentStatus.SUCCEEDED;
        this.domainEvents.add(PaymentReceived.now(id, orderId, amount));
    }

    public void markFailed() {
        if (status != PaymentStatus.INITIATED) {
            throw new IllegalStateException("Only an INITIATED payment can fail (was: " + status + ")");
        }
        this.status = PaymentStatus.FAILED;
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> pulled = List.copyOf(domainEvents);
        domainEvents.clear();
        return pulled;
    }

    public PaymentId id() { return id; }
    public OrderId orderId() { return orderId; }
    public Money amount() { return amount; }
    public PaymentStatus status() { return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}