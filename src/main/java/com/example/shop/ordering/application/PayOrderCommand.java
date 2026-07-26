package com.example.shop.ordering.application;

/**
 * PayOrderCommand — the intent to pay an order, as data.
 *
 * <p>In a real system, this command would carry payment details (credit card, etc.).
 * Here we keep it simple and just identify the order to pay.
 */
public record PayOrderCommand(String orderId) {
}