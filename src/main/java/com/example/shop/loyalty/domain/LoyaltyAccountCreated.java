package com.example.shop.loyalty.domain;

import com.example.shop.shared.DomainEvent;
import java.time.Instant;

public record LoyaltyAccountCreated(LoyaltyAccountId id, CustomerId customerId, Instant occurredOn) implements DomainEvent {
    public static DomainEvent now(LoyaltyAccountId id, CustomerId customerId) {
        return new LoyaltyAccountCreated(id, customerId, Instant.now());
    }
}
