package com.example.shop.ordering.domain;

/**
 * OrderStatus — the lifecycle states of an Order, in the language of the business.
 *
 * <p>Names, not numbers. "PLACED" speaks to a domain expert; "status = 7" does not.
 */
public enum OrderStatus {
    PLACED,
    PAID,
    SHIPPED,
    CANCELLED
}