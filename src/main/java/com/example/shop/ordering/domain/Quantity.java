package com.example.shop.ordering.domain;

/**
 * Quantity — a Value Object for a positive count of items.
 *
 * <p>Better than a raw int: it guarantees the "must be positive" rule in ONE place,
 * so no OrderLine can ever hold a zero or negative quantity.
 */
public record Quantity(int value) {

    public Quantity {
        if (value <= 0) {
            throw new IllegalArgumentException("Quantity must be positive, got: " + value);
        }
    }

    public static Quantity of(int value) {
        return new Quantity(value);
    }

    public Quantity plus(Quantity other) {
        return new Quantity(this.value + other.value);
    }
}