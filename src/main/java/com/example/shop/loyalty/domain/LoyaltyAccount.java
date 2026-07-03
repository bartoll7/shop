package com.example.shop.loyalty.domain;

import com.example.shop.shared.DomainEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoyaltyAccount {
    private final LoyaltyAccountId id;
    private final CustomerId customerId;
    private int points;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private LoyaltyAccount(LoyaltyAccountId id, CustomerId customerId, int points) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.customerId = Objects.requireNonNull(customerId, "customer id must not be null");
        this.points = points;
    }

    public static LoyaltyAccount create(CustomerId customerId) {
        LoyaltyAccount account = new LoyaltyAccount(LoyaltyAccountId.newId(), customerId, 0);
        account.domainEvents.add(LoyaltyAccountCreated.now(account.id(), customerId));
        return account;
    }

    public static LoyaltyAccount reconstitute(LoyaltyAccountId id, CustomerId customerId, int points) {
        return new LoyaltyAccount(id, customerId, points);
    }

    public void award(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points to award must be positive");
        }
        this.points += points;
        domainEvents.add(LoyaltyPointsAwarded.now(this.id, this.customerId, points));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoyaltyAccount other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "LoyaltyAccount{" +
            "id=" + id +
            ", customerId=" + customerId +
            ", points=" + points +
            '}';
    }

    public LoyaltyAccountId id() {
        return id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public int points() {
        return points;
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> pulled = List.copyOf(domainEvents);
        domainEvents.clear();
        return pulled;
    }
}
