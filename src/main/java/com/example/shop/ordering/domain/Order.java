package com.example.shop.ordering.domain;

import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.Money;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Order — Aggregate Root.
 *
 * <p>The single entry point to its aggregate. Nobody outside touches an OrderLine
 * directly: all changes go through Order, which guards the consistency boundary.
 *
 * <p>Invariants enforced here (must always hold, together):
 * <ul>
 *   <li>Lines can only be added/changed while the order is PLACED.</li>
 *   <li>Adding a product that's already present merges into the existing line
 *       (our domain decision) — a rule that needs a view of the WHOLE order.</li>
 *   <li>All lines share the order's currency.</li>
 *   <li>An order must have at least one line before it can be paid.</li>
 *   <li>The total is always derived from the lines — never set independently.</li>
 * </ul>
 */
public class Order {

    private final OrderId id;
    private final CustomerId customerId;
    private OrderStatus status;
    private final List<OrderLine> lines;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Order(OrderId id, CustomerId customerId, OrderStatus status, List<OrderLine> lines) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.customerId = Objects.requireNonNull(customerId, "customer id must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.lines = new ArrayList<>(Objects.requireNonNull(lines, "lines must not be null"));
    }

    public static Order place(CustomerId customerId) {
        Order order = new Order(OrderId.newId(), customerId, OrderStatus.PLACED, new ArrayList<>());
        order.domainEvents.add(OrderPlaced.now(order.id(), customerId));
        return order;
    }

    /**
     * Reconstitute an existing order from persisted state. Used ONLY by repository
     * adapters when loading from storage — not for creating new orders (use place()).
     * Intention-revealing, so reconstruction never gets confused with creation.
     */
    public static Order reconstitute(OrderId id, CustomerId customerId, OrderStatus status, java.util.List<OrderLine> lines) {
        return new Order(id, customerId, status, lines);
    }

    // --- Aggregate behavior: all mutations go through the root ---

    /**
     * Add a product to the order. If the same ProductId is already present, the
     * quantities are merged into the existing line. Only allowed while PLACED.
     */
    public void addLine(ProductId productId, String productName, Money unitPrice, Quantity quantity) {
        if (status != OrderStatus.PLACED) {
            throw new IllegalStateException("Cannot modify lines of a " + status + " order");
        }
        // Currency consistency: a new line must match lines already in the order.
        if (!lines.isEmpty() && !lines.getFirst().unitPrice().currency().equals(unitPrice.currency())) {
            throw new IllegalArgumentException("All order lines must share the same currency");
        }

        int existingIndex = indexOfProduct(productId);
        if (existingIndex >= 0) {
            // Merge: replace the existing line with one of increased quantity.
            OrderLine merged = lines.get(existingIndex).withAdditionalQuantity(quantity);
            lines.set(existingIndex, merged);
        } else {
            lines.add(new OrderLine(productId, productName, unitPrice, quantity));
        }
    }

    public void markAsPaid() {
        if (status != OrderStatus.PLACED) {
            throw new IllegalStateException("Only a PLACED order can be paid (was: " + status + ")");
        }
        if (lines.isEmpty()) {
            throw new IllegalStateException("Cannot pay an order with no lines");
        }
        this.status = OrderStatus.PAID;
        this.domainEvents.add(OrderPaid.now(id, totalAmount()));
    }

    public void markAsShipped() {
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException("Only a PAID order can be shipped (was: " + status + ")");
        }
        this.status = OrderStatus.SHIPPED;
    }

    public void cancel() {
        if (status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("A SHIPPED order cannot be cancelled");
        }
        this.status = OrderStatus.CANCELLED;
    }

    /** The order total, always derived from its lines. */
    public Money totalAmount() {
        if (lines.isEmpty()) {
            throw new IllegalStateException("An empty order has no total");
        }
        Money total = lines.getFirst().lineTotal();
        for (int i = 1; i < lines.size(); i++) {
            total = total.add(lines.get(i).lineTotal());
        }
        return total;
    }

    private int indexOfProduct(ProductId productId) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).productId().equals(productId)) {
                return i;
            }
        }
        return -1;
    }

    public OrderId id() {
        return id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public OrderStatus status() {
        return status;
    }

    /** Lines are exposed as an UNMODIFIABLE view, so nobody can bypass the root. */
    public List<OrderLine> lines() {
        return Collections.unmodifiableList(lines);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> pulled = List.copyOf(domainEvents);
        domainEvents.clear();
        return pulled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Order{id=%s, status=%s, lines=%d}".formatted(id, status, lines.size());
    }
}