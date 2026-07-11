package com.example.shop.ordering.domain;

import com.example.shop.shared.Money;
import java.math.BigDecimal;
import java.util.Objects;

public class PercentageOffAboveThreshold implements DiscountPolicy {
    private final Money threshold;
    private final BigDecimal percentage;

    public PercentageOffAboveThreshold(Money threshold, BigDecimal percentage) {
        this.threshold = threshold;
        if (percentage.signum() <= 0 || percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }

        if (percentage.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Percentage must be a whole number");
        }

        this.percentage = Objects.requireNonNull(percentage);

    }

    @Override
    public Money discountFor(Order order) {
        if (order.totalAmount().isGreaterThan(threshold)) {
            return order.totalAmount().multiply(percentage.divide(BigDecimal.valueOf(100)));
        } else {
            return new Money(BigDecimal.ZERO, order.totalAmount().currency());
        }
    }
}
