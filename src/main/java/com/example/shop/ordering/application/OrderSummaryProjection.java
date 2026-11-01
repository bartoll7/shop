package com.example.shop.ordering.application;

import com.example.shop.ordering.domain.OrderPaid;
import com.example.shop.ordering.domain.OrderPlaced;
import com.example.shop.ordering.domain.OrderStatus;
import com.example.shop.ordering.query.OrderSummary;
import com.example.shop.ordering.query.OrderSummaryQuery;
import com.example.shop.ordering.query.OrderSummaryUpsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderSummaryProjection {
    private final OrderSummaryUpsert orderSummaryUpsert;
    private final OrderSummaryQuery orderSummaryQuery;

    public OrderSummaryProjection(OrderSummaryUpsert orderSummaryUpsert, OrderSummaryQuery orderSummaryQuery) {
        this.orderSummaryUpsert = orderSummaryUpsert;
        this.orderSummaryQuery = orderSummaryQuery;
    }

    @TransactionalEventListener
    public void on(OrderPlaced event) {
        orderSummaryUpsert.save(
            new OrderSummary(event.orderId(), event.customerId(), OrderStatus.PLACED, event.lineCount(), event.total(), event.occurredOn()));
    }

    @TransactionalEventListener
    public void on(OrderPaid event) {
        var orderSummary = orderSummaryQuery.byId(event.orderId()).orElseThrow();
        orderSummaryUpsert.save(
            new OrderSummary(orderSummary.id(), orderSummary.customerId(), OrderStatus.PAID, orderSummary.lineCount(), orderSummary.total(), orderSummary.placedAt())
        );
    }
}
