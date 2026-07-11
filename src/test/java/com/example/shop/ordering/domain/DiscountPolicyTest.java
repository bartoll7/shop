package com.example.shop.ordering.domain;

import com.example.shop.shared.Money;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class DiscountPolicyTest {
    private static final CustomerId CUSTOMER = CustomerId.newId();

    private static Order orderWorth(String amount, String currency) {
        Order order = Order.place(CUSTOMER);
        order.addLine(ProductId.newId(), "Wino X", Money.of(amount, currency), Quantity.of(1));
        return order;
    }

    @Test
    void noDiscountLeavesTheTotalUntouched() {
        Order order = orderWorth("150.00", "EUR");

        Money net = order.netTotal(new NoDiscount());

        assertThat(net).isEqualTo(Money.of("150.00", "EUR"));
    }

    @Test
    void percentageOffAboveThreshold() {
        Order order = orderWorth("120.00", "PLN");
        DiscountPolicy policy = new PercentageOffAboveThreshold(Money.of("100.00", "PLN"), BigDecimal.valueOf(10));

        Money net = order.netTotal(policy);

        assertThat(net).isEqualTo(Money.of("108.00", "PLN"));
    }

    @Test
    void percentageOffEqualsThresholdMeansNoDiscount() {
        Order order = orderWorth("100.00", "PLN");
        DiscountPolicy policy = new PercentageOffAboveThreshold(Money.of("100.00", "PLN"), BigDecimal.valueOf(10));

        Money net = order.netTotal(policy);

        assertThat(net).isEqualTo(Money.of("100.00", "PLN"));
    }

    @Test
    void percentageOffBelowThresholdMeansNoDiscount() {
        Order order = orderWorth("100.00", "PLN");
        DiscountPolicy policy = new PercentageOffAboveThreshold(Money.of("150.00", "PLN"), BigDecimal.valueOf(10));

        Money net = order.netTotal(policy);

        assertThat(net).isEqualTo(Money.of("100.00", "PLN"));
    }

    @Test
    void percentageOffEqualsThresholdMeans100PercentageDiscount() {
        Order order = orderWorth("100.00", "PLN");
        DiscountPolicy policy = new PercentageOffAboveThreshold(Money.of("90.00", "PLN"), BigDecimal.valueOf(100));

        Money net = order.netTotal(policy);

        assertThat(net).isEqualTo(Money.of("0.00", "PLN"));
    }

    @Test
    void discountGreaterThanTotalThrows() {
        Order order = orderWorth("120.00", "PLN");
        DiscountPolicy tooGreedy = _ -> Money.of("999.00", "PLN");

        assertThatThrownBy(() -> order.netTotal(tooGreedy))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void discountInDifferentCurrencyThrows() {
        Order order = orderWorth("120.00", "PLN");
        DiscountPolicy wrongCurrency = _ -> Money.of("10.00", "EUR");

        assertThatThrownBy(() -> order.netTotal(wrongCurrency))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
