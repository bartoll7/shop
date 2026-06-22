package com.example.shop.payment.domain;

import java.util.Objects;
import java.util.UUID;

public record PaymentId(UUID value) {

    public PaymentId {
        Objects.requireNonNull(value, "PaymentId value must not be null");
    }

    public static PaymentId newId() {
        return new PaymentId(UUID.randomUUID());
    }

    public static PaymentId of(String raw) {
        return new PaymentId(UUID.fromString(raw));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}