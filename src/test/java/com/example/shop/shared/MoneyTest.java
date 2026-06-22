package com.example.shop.shared;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void twoMoniesWithSameAmountAndCurrencyAreEqual() {
        // Value-based equality: this is the essence of a Value Object.
        // No identity — same values means interchangeable.
        assertThat(Money.of("50.00", "PLN"))
                .isEqualTo(Money.of("50.00", "PLN"));
    }

    @Test
    void scaleIsNormalizedSoThatTrailingZerosDoNotMatter() {
        // 50 PLN and 50.00 PLN are the same money.
        assertThat(Money.of("50", "PLN"))
                .isEqualTo(Money.of("50.00", "PLN"));
    }

    @Test
    void addingSameCurrencyReturnsANewSummedMoney() {
        Money result = Money.of("50.00", "PLN").add(Money.of("10.00", "PLN"));
        assertThat(result).isEqualTo(Money.of("60.00", "PLN"));
    }

    @Test
    void addingIsImmutable_originalIsUnchanged() {
        Money fifty = Money.of("50.00", "PLN");
        fifty.add(Money.of("10.00", "PLN")); // result deliberately ignored
        // The original object was NOT mutated — it is still 50.
        assertThat(fifty).isEqualTo(Money.of("50.00", "PLN"));
    }

    @Test
    void multiplyByQuantity() {
        Money unitPrice = Money.of("19.99", "PLN");
        assertThat(unitPrice.multiply(3)).isEqualTo(Money.of("59.97", "PLN"));
    }

    @Test
    void addingDifferentCurrenciesIsADomainError() {
        // The decision we made: mixing currencies must blow up, not guess.
        assertThatThrownBy(() ->
                Money.of("50.00", "PLN").add(Money.of("10.00", "EUR")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("different currencies");
    }

    @Test
    void amountMustNotBeNull() {
        assertThatThrownBy(() -> new Money(null, java.util.Currency.getInstance("PLN")))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void canDetectNegativeMoney() {
        Money debt = Money.of(new BigDecimal("-5.00"), "PLN");
        assertThat(debt.isNegative()).isTrue();
    }
}
