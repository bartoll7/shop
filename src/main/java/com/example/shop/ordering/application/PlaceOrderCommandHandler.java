package com.example.shop.ordering.application;

import com.example.shop.agerestriction.domain.PurchaseEligibility;
import com.example.shop.ordering.domain.*;
import com.example.shop.shared.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PlaceOrderCommandHandler — orchestrates the place order use case.
 * <p>It coordinates; it does NOT contain business rules.
 * The aggregate is created, its state is changed, and the new state is persisted. Any domain events are published.
 */
@Service
public class PlaceOrderCommandHandler {
    private final OrderRepository orders;
    private final DomainEventPublisher events;
    private final PurchaseEligibility purchaseEligibility;

    public PlaceOrderCommandHandler(OrderRepository orders, DomainEventPublisher events, PurchaseEligibility purchaseEligibility) {
        this.orders = orders;
        this.events = events;
        this.purchaseEligibility = purchaseEligibility;
    }

    @Transactional
    public OrderId handle(PlaceOrderCommand command) {
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

        Order order = Order.place(CustomerId.of(command.customerId()));
        for (PlaceOrderCommand.Item item : command.items()) {
            order.addLine(
                ProductId.of(item.productId()),
                item.productName(),
                item.unitPrice(),
                Quantity.of(item.quantity())
            );
        }
        orders.save(order);
        events.publishAll(order.pullDomainEvents());
        return order.id();
    }
}
