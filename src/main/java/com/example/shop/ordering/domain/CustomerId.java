package com.example.shop.ordering.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * CustomerId — Value Object identifying an Customer.
 *
 * <p>A distinct type from OrderId (and any other id) so the compiler rejects
 * mixing them. The id is a VO; the Customer it points at is an Entity.
 */
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

    @Override
    public String toString() {
        return value.toString();
    }
}