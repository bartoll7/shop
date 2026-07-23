package com.example.shop.returns.domain;

import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.Money;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Return {
    private final ReturnId id;
    private final OrderId orderId;
    private ReturnStatus status;
    private final Money refundAmount;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Return(ReturnId id, OrderId orderId, ReturnStatus status, Money refundAmount) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderId = Objects.requireNonNull(orderId, "order id must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.refundAmount = Objects.requireNonNull(refundAmount, "refund amount must not be null");
    }

    public static Return request(OrderId orderId, Money refundAmount) {
        if (refundAmount.isNegative() || refundAmount.isZero()) {
            throw new IllegalArgumentException("Refund amount must not be negative or zero");
        }

        Return returnAggregate = new Return(ReturnId.newId(), orderId, ReturnStatus.REQUESTED, refundAmount);
        returnAggregate.domainEvents.add(ReturnRequested.now(returnAggregate.id(), orderId, refundAmount));
        return returnAggregate;
    }

    /**
     * Reconstitute an existing return from persisted state. Used ONLY by repository
     * adapters when loading from storage — not for creating new returns (use request()).
     * Intention-revealing, so reconstruction never gets confused with creation.
     */
    public static Return reconstitute(ReturnId id, OrderId orderId, ReturnStatus status, Money refundAmount) {
        return new Return(id, orderId, status, refundAmount);
    }

    public void markAsSent() {
        if (status != ReturnStatus.REQUESTED) {
            throw new IllegalStateException("Only a REQUESTED return can be sent (was: " + status + ")");
        }
        this.status = ReturnStatus.SENT;
        this.domainEvents.add(ReturnSent.now(id, orderId, refundAmount));
    }

    public void markAsDelivered() {
        if (status != ReturnStatus.SENT) {
            throw new IllegalStateException("Only a SENT return can be delivered (was: " + status + ")");
        }
        this.status = ReturnStatus.DELIVERED;
    }

    public void accept() {
        if (status != ReturnStatus.DELIVERED) {
            throw new IllegalStateException("Only a DELIVERED return can be accepted (was: " + status + ")");
        }
        this.status = ReturnStatus.ACCEPTED;
        this.domainEvents.add(ReturnAccepted.now(id, orderId, refundAmount));
    }

    public void reject() {
        if (status != ReturnStatus.DELIVERED) {
            throw new IllegalStateException("Only a DELIVERED return can be rejected (was: " + status + ")");
        }
        this.status = ReturnStatus.REJECTED;
        this.domainEvents.add(ReturnRejected.now(id, orderId));
    }

    public ReturnId id() {
        return id;
    }

    public OrderId orderId() {
        return orderId;
    }

    public ReturnStatus status() {
        return status;
    }

    public Money refundAmount() {
        return refundAmount;
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> pulled = List.copyOf(domainEvents);
        domainEvents.clear();
        return pulled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Return other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Return{id=%s, orderId=%s, status=%s}".formatted(id, orderId, status);
    }
}
