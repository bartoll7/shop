package com.example.shop.ordering.application;

import com.example.shop.agerestriction.domain.PurchaseEligibility;
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
 * <p>It coordinates; it does NOT contain business rules. For placeOrder it now also
 * enforces the age-restriction rule by invoking the PurchaseEligibility domain service
 * (Customer-Supplier relationship: ordering consumes the age-restriction context).
 * The check happens BEFORE the aggregate is built and saved, so a disallowed order is
 * rejected without any persistence or events.
 */
@Service
public class OrderApplicationService {

    private final OrderRepository orders;
    private final DomainEventPublisher events;
    private final PurchaseEligibility purchaseEligibility;

    public OrderApplicationService(OrderRepository orders,
        DomainEventPublisher events,
        PurchaseEligibility purchaseEligibility) {
        this.orders = orders;
        this.events = events;
        this.purchaseEligibility = purchaseEligibility;
    }

    @Transactional
    public OrderId placeOrder(PlaceOrderCommand command) {
        // Enforce age restriction across all items before doing anything else.
        for (PlaceOrderCommand.Item item : command.items()) {
            boolean allowed = purchaseEligibility.isAllowed(
                command.buyerAge(),
                item.ageRestriction(),
                command.shippingCountry());
            if (!allowed) {
                throw new AgeRestrictionViolation(
                    "Buyer (age %d) may not purchase '%s' shipped to %s"
                        .formatted(command.buyerAge(), item.productName(),
                                   command.shippingCountry().code()));
            }
        }

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