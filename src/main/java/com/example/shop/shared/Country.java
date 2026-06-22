package com.example.shop.shared;

import java.util.Objects;
import java.util.Set;

/**
 * Country — a Value Object holding an ISO 3166-1 alpha-2 country code (e.g. "PL", "DE").
 *
 * <p>Wrapping the code (instead of a raw String) gives us one place to validate
 * the format and frees the rest of the model from worrying about "PL" vs "pl"
 * vs "Poland" vs typos. This is the country that, for a shipping address, will
 * determine the jurisdiction for age restriction (week 1 decision).
 */
public record Country(String code) {

    // A tiny allow-list to keep the example honest without pulling in a library.
    // In a real system you'd validate against the full ISO list.
    private static final Set<String> KNOWN = Set.of("PL", "DE", "GB", "FR", "ES", "SE", "LU", "CY", "MT", "BE", "DK", "AT", "US");

    public Country {
        Objects.requireNonNull(code, "country code must not be null");
        code = code.trim().toUpperCase();
        if (code.length() != 2) {
            throw new IllegalArgumentException("Country code must be 2 letters (ISO alpha-2), got: " + code);
        }
        if (!KNOWN.contains(code)) {
            throw new IllegalArgumentException("Unknown country code: " + code);
        }
    }

    public static Country of(String code) {
        return new Country(code);
    }
}