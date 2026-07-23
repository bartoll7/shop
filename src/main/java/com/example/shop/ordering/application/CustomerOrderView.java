package com.example.shop.ordering.application;

import com.example.shop.ordering.domain.OrderId;
import com.example.shop.ordering.domain.OrderStatus;
import com.example.shop.shared.Money;

public record CustomerOrderView(OrderId orderId, OrderStatus status, int lineCount, Money total, String firstProductName) {
}
