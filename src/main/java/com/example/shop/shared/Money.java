package com.example.shop.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Money — a Value Object.
 *
 * <p>Defined entirely by its values (amount + currency). Two Money instances
 * with the same amount and currency are equal and interchangeable — there is
 * no identity to track. As a {@code record}, equals/hashCode are value-based
 * for free, which is exactly the semantics we want.
 *
 * <p>Immutable: every operation returns a NEW Money. You never mutate money,
 * just as you never "edit" a banknote — you reach for a different one.
 *
 * <p>Guards its own invariants: amount is never null, currency is never null,
 * and arithmetic across different currencies is rejected as a domain error.
 */
public record Money(BigDecimal amount, Currency currency) {

    // Compact constructor: runs on every creation, including via "with..." methods.
    // This is where the Value Object defends its invariants.
    public Money {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        // Normalize scale to the currency's default (e.g. 2 for PLN/EUR/USD)
        // so that 10.5 PLN and 10.50 PLN are considered equal.
        amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_EVEN);
    }

    // --- Convenient factory methods (read better than calling the constructor) ---

    public static Money of(String amount, String currencyCode) {
        return new Money(new BigDecimal(amount), Currency.getInstance(currencyCode));
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, Currency.getInstance(currencyCode));
    }

    public static Money zero(String currencyCode) {
        return new Money(BigDecimal.ZERO, Currency.getInstance(currencyCode));
    }

    // --- Behavior. A VO is not just data; it knows how to operate on itself. ---

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    /** Multiply by a plain quantity (e.g. unit price × number of items). */
    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }

    /** Multiply by an arbitrary factor (e.g. a percentage expressed as a fraction). */
    public Money multiply(BigDecimal factor) {
        return new Money(this.amount.multiply(factor), this.currency);
    }

    public boolean isGreaterThan(Money other) {
        requireSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isNegative() {
        return this.amount.signum() < 0;
    }

    public boolean isZero() {
        return this.amount.signum() == 0;
    }

    // --- Invariant enforcement ---

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "other money must not be null");
        if (!this.currency.equals(other.currency)) {
            // Mixing currencies is a domain error: it hides a missing decision
            // (which exchange rate? whose? when?). Fail fast and loud.
            throw new IllegalArgumentException(
                    "Cannot operate on different currencies: %s and %s"
                            .formatted(this.currency, other.currency));
        }
    }
}
