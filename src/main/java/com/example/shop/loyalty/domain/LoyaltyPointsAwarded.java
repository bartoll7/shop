package com.example.shop.loyalty.domain;

import com.example.shop.shared.DomainEvent;
import java.time.Instant;

public record LoyaltyPointsAwarded(LoyaltyAccountId id, CustomerId customerId, int points, Instant occurredOn) implements DomainEvent {
    public static DomainEvent now(LoyaltyAccountId id, CustomerId customerId, int points) {
        return new LoyaltyPointsAwarded(id, customerId, points, Instant.now());
    }
}
