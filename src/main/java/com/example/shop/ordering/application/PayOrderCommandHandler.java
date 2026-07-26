package com.example.shop.ordering.application;

import com.example.shop.ordering.domain.Order;
import com.example.shop.ordering.domain.OrderId;
import com.example.shop.ordering.domain.OrderRepository;
import com.example.shop.shared.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PayOrderCommandHandler — orchestrates the pay order use case.
 *
 * <p>It coordinates; it does NOT contain business rules. The aggregate is loaded, its state
 * is changed, and the new state is persisted. Any domain events are published.
 */
@Service
public class PayOrderCommandHandler {
    private final OrderRepository orders;
    private final DomainEventPublisher events;

    public PayOrderCommandHandler(OrderRepository orders, DomainEventPublisher events) {
        this.orders = orders;
        this.events = events;
    }

    @Transactional
    public void handle(PayOrderCommand command) {
        OrderId orderId = OrderId.of(command.orderId());
        Order order = orders.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown order: " + orderId));
        order.markAsPaid();
        orders.save(order);
        events.publishAll(order.pullDomainEvents());
    }
}
