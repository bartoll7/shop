package com.example.shop.ordering.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * OrderId — Value Object identifying an Order.
 *
 * <p>A distinct type from ProductId (and any other id) so the compiler rejects
 * mixing them. The id is a VO; the Order it points at is an Entity.
 */
public record OrderId(UUID value) {

    public OrderId {
        Objects.requireNonNull(value, "OrderId value must not be null");
    }

    public static OrderId newId() {
        return new OrderId(UUID.randomUUID());
    }

    public static OrderId of(String raw) {
        return new OrderId(UUID.fromString(raw));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}