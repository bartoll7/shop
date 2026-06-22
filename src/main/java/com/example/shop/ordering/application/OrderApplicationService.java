package com.example.shop.ordering.application;

import com.example.shop.ordering.domain.Order;
import com.example.shop.ordering.domain.OrderId;
import com.example.shop.ordering.domain.OrderRepository;
import com.example.shop.ordering.domain.ProductId;
import com.example.shop.ordering.domain.Quantity;
import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * OrderApplicationService — orchestrates order use cases.
 *
 * <p>It coordinates; it does NOT contain business rules (those live in the domain).
 * Per use case it: loads/creates aggregates via the repository PORT, invokes domain
 * behavior, saves, then publishes the recorded domain events — AFTER the save, so
 * events only escape once the change is durable.
 *
 * <p>It depends on PORTS (OrderRepository, DomainEventPublisher), never on concrete
 * infrastructure. Spring wires the adapters in at runtime.
 */
@Service
public class OrderApplicationService {

    private final OrderRepository orders;
    private final DomainEventPublisher events;

    public OrderApplicationService(OrderRepository orders, DomainEventPublisher events) {
        this.orders = orders;
        this.events = events;
    }

    @Transactional
    public OrderId placeOrder(PlaceOrderCommand command) {
        Order order = Order.place();
        for (PlaceOrderCommand.Item item : command.items()) {
            order.addLine(
                ProductId.of(item.productId()),
                item.productName(),
                item.unitPrice(),
                Quantity.of(item.quantity())
            );
        }
        orders.save(order);
        publishEvents(order.pullDomainEvents());
        return order.id();
    }

    @Transactional
    public void payOrder(OrderId orderId) {
        Order order = orders.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown order: " + orderId));
        order.markAsPaid();
        orders.save(order);
        publishEvents(order.pullDomainEvents());
    }

    private void publishEvents(List<DomainEvent> domainEvents) {
        for (DomainEvent event : domainEvents) {
            events.publish(event);
        }
    }
}