package com.example.shop.ordering.application;

import com.example.shop.shared.Money;

import java.util.List;

/**
 * PlaceOrderCommand — the intent to place an order, as data.
 *
 * <p>A command is a request to DO something (imperative), as opposed to a domain
 * event, which records something that HAS happened (past tense).
 */
public record PlaceOrderCommand(List<Item> items) {

    public record Item(String productId, String productName, Money unitPrice, int quantity) {
    }
}