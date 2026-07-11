package com.example.shop.ordering.domain;

import com.example.shop.shared.Money;
import java.math.BigDecimal;

public class NoDiscount implements DiscountPolicy {
    @Override
    public Money discountFor(Order order) {
        return new Money(BigDecimal.ZERO, order.totalAmount().currency());
    }
}
