package com.example.shop.loyalty.domain;

import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {

    public CustomerId {
        Objects.requireNonNull(value, "CustomerId value must not be null");
    }

    public static CustomerId newId() {
        return new CustomerId(UUID.randomUUID());
    }

    public static CustomerId of(String raw) {
        return new CustomerId(UUID.fromString(raw));
    }

    public static CustomerId of(UUID uuid) {
        return new CustomerId(uuid);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}