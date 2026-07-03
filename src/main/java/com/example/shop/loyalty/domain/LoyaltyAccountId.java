package com.example.shop.loyalty.domain;

import java.util.Objects;
import java.util.UUID;

public record LoyaltyAccountId(UUID value) {

    public LoyaltyAccountId {
        Objects.requireNonNull(value, "LoyaltyAccountId value must not be null");
    }

    public static LoyaltyAccountId newId() {
        return new LoyaltyAccountId(UUID.randomUUID());
    }

    public static LoyaltyAccountId of(String raw) {
        return new LoyaltyAccountId(UUID.fromString(raw));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}