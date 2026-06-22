package com.example.shop.ordering.domain;

import com.example.shop.shared.Money;

import java.util.Objects;

/**
 * OrderLine — a line inside the Order aggregate.
 *
 * <p>Key aggregate-design decisions made visible here:
 * <ul>
 *   <li>Holds a {@link ProductId} — a reference BY ID to the separate Product
 *       aggregate, never the Product object itself.</li>
 *   <li>Holds a SNAPSHOT of the product name and unit price as they were at the
 *       moment of purchase. These belong to the Order forever and are immune to
 *       later catalog changes. "Purchase price" is a different concept from
 *       "catalog price".</li>
 *   <li>Immutable: increasing the quantity returns a NEW OrderLine, like Money.add().</li>
 * </ul>
 */
public record OrderLine(
    ProductId productId,
    String productName,   // snapshot at purchase time
    Money unitPrice,      // snapshot at purchase time ("purchase price")
    Quantity quantity) {

    public OrderLine {
        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(productName, "productName must not be null");
        Objects.requireNonNull(unitPrice, "unitPrice must not be null");
        Objects.requireNonNull(quantity, "quantity must not be null");
        if (productName.isBlank()) {
            throw new IllegalArgumentException("productName must not be blank");
        }
        if (unitPrice.isNegative()) {
            throw new IllegalArgumentException("unitPrice must not be negative");
        }
    }

    /** The total cost of this line: unit price × quantity. */
    public Money lineTotal() {
        return unitPrice.multiply(quantity.value());
    }

    /** Return a new line with the quantity increased — immutable update. */
    public OrderLine withAdditionalQuantity(Quantity extra) {
        return new OrderLine(productId, productName, unitPrice, quantity.plus(extra));
    }
}