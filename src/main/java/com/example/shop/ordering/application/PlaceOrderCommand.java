package com.example.shop.ordering.application;

import com.example.shop.agerestriction.domain.AgeRestriction;
import com.example.shop.shared.Country;
import com.example.shop.shared.Money;

import java.util.List;

/**
 * PlaceOrderCommand — the intent to place an order, as data.
 *
 * <p>Carries buyer age and shipping country so the application service can enforce
 * the age-restriction rule at placement time.
 *
 * <p>NOTE (modeling shortcut): in a full system, buyerAge would come from the Customer
 * context and each item's AgeRestriction from the product catalog (looked up by
 * ProductId). Here they arrive in the command to keep the example focused on WHERE the
 * domain service is invoked, not on building extra contexts.
 */
public record PlaceOrderCommand(int buyerAge, Country shippingCountry, List<Item> items) {

    public record Item(String productId,
                       String productName,
                       Money unitPrice,
                       int quantity,
                       AgeRestriction ageRestriction) {
    }
}