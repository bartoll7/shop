package com.example.shop.ordering.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * ProductId — a Value Object wrapping the identity of a Product.
 *
 * <p>Why a whole type instead of a raw UUID/Long? Because a raw id is
 * interchangeable with every other raw id of the same primitive type. Wrapping
 * it makes the compiler reject mixing a ProductId with, say, an OrderId — a whole
 * class of bugs becomes impossible to even write.
 *
 * <p>Note the subtlety: a Product is an ENTITY (it has identity that outlives its
 * attributes), but its identifier — ProductId — is itself a VALUE OBJECT. The id
 * *points at* identity; it does not *have* identity of its own.
 */
public record ProductId(UUID value) {

    public ProductId {
        Objects.requireNonNull(value, "ProductId value must not be null");
    }

    /** Generate a brand-new identity (e.g. when creating a new Product). */
    public static ProductId newId() {
        return new ProductId(UUID.randomUUID());
    }

    /** Reconstruct from a stored/string form (e.g. when loading from the DB). */
    public static ProductId of(String raw) {
        return new ProductId(UUID.fromString(raw));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}