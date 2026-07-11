package com.example.shop.ordering.domain;

import com.example.shop.shared.Money;

/**
 * Represents a discount policy that can be applied to an order.
 */
public interface DiscountPolicy {
    /**
     * Calculates the discount amount for the given order based on the policy.
     *
     * @param order the order for which the discount is to be calculated
     * @return the discount amount as a Money object
     */
    Money discountFor(Order order);
}
