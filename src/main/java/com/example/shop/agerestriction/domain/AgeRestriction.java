package com.example.shop.agerestriction.domain;

import com.example.shop.shared.Country;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * AgeRestriction — a Value Object describing the minimum purchase age for a product,
 * per jurisdiction (identified by the shipping Country — the week-1 decision).
 *
 * <p>"Minimum purchase age" is deliberately NOT called "adulthood": it is the legal
 * age to BUY this kind of product in a given jurisdiction, which may differ from
 * full legal adulthood and varies by country/product.
 *
 * <p>Supporting subdomain: modeled cleanly but frugally.
 */
public record AgeRestriction(Map<Country, Integer> minimumAgeByCountry) {

    public AgeRestriction {
        Objects.requireNonNull(minimumAgeByCountry, "minimumAgeByCountry must not be null");
        minimumAgeByCountry = Map.copyOf(minimumAgeByCountry); // defensive, immutable
    }

    /** A product with no age restriction anywhere. */
    public static AgeRestriction none() {
        return new AgeRestriction(Map.of());
    }

    /**
     * The minimum purchase age required in the given jurisdiction, if any.
     * Empty means: no restriction for this country.
     */
    public Optional<Integer> minimumAgeFor(Country shippingCountry) {
        return Optional.ofNullable(minimumAgeByCountry.get(shippingCountry));
    }
}