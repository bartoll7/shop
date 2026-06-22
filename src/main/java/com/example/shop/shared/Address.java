package com.example.shop.shared;

import java.util.Objects;

/**
 * Address — a Value Object.
 *
 * <p>In our shop, an address has no identity of its own: changing the house number
 * doesn't give you "the same address, evolved" — it gives you a *different* address.
 * So it's a VO, defined wholly by its fields. (In a postal/geodetic domain it could
 * be an Entity — the classification depends on the domain, not the concept.)
 *
 * <p>This VO guards a *multi-field* invariant: the whole set of values must form a
 * coherent address. We reject blank required parts rather than allowing a
 * half-built address to exist.
 */
public record Address(
    String street,
    String houseNumber,
    String postalCode,
    String city,
    Country country) {

    public Address {
        street = requireText(street, "street");
        houseNumber = requireText(houseNumber, "houseNumber");
        postalCode = requireText(postalCode, "postalCode");
        city = requireText(city, "city");
        Objects.requireNonNull(country, "country must not be null");
    }

    private static String requireText(String value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return trimmed;
    }
}