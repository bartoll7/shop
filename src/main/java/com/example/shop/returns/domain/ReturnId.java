package com.example.shop.returns.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * ReturnId — Value Object identifying a Return.
 *
 * The id is a VO; the Return it points at is an Entity.
 */
public record ReturnId(UUID value) {

    public ReturnId {
        Objects.requireNonNull(value, "ReturnId value must not be null");
    }

    public static ReturnId newId() {
        return new ReturnId(UUID.randomUUID());
    }

    public static ReturnId of(String raw) {
        return new ReturnId(UUID.fromString(raw));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}