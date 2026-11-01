package com.example.shop.ordering.domain;

import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.Money;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {
    private static final CustomerId CUSTOMER = CustomerId.newId();

    private static List<OrderLine> oneLine() {
        return List.of(new OrderLine(ProductId.newId(), "Wino X", Money.of("50.00", "PLN"), Quantity.of(1)));
    }

    private static List<OrderLine> sameLines() {
        ProductId productId = ProductId.newId();

        return List.of(
            new OrderLine(productId, "Wino X", Money.of("50.00", "PLN"), Quantity.of(1)),
            new OrderLine(productId, "Wino X", Money.of("50.00", "PLN"), Quantity.of(1))
        );
    }


    @Test
    void sameIdentityMeansEqual_evenWithDifferentState() {
        // The defining trait of an Entity: identity, not values, decides equality.
        Order order = Order.place(CUSTOMER, oneLine());

        // Reconstruct a different object that shares the SAME id but is PAID.
        Order samePaid = rebuild(order.id(), OrderStatus.PAID);

        // Different status, but same identity -> the SAME order.
        assertThat(order).isEqualTo(samePaid);
    }

    @Test
    void differentIdentityMeansNotEqual_evenWithIdenticalState() {
        // Two freshly placed orders have identical state (both PLACED)...
        Order a = Order.place(CUSTOMER, oneLine());
        Order b = Order.place(CUSTOMER, oneLine());
        // ...but different identity -> different orders. Opposite of a Value Object.
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void aPlacedOrderCanBePaidThenShipped() {
        Order order = Order.place(CUSTOMER, oneLine());
        order.markAsPaid();
        order.markAsShipped();
        assertThat(order.status()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void cannotShipAnUnpaidOrder() {
        Order order = Order.place(CUSTOMER, oneLine());
        assertThatThrownBy(order::markAsShipped)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("PAID");
    }

    @Test
    void cannotCancelAShippedOrder() {
        Order order = Order.place(CUSTOMER, oneLine());
        order.markAsPaid();
        order.markAsShipped();
        assertThatThrownBy(order::cancel)
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void placingOrderWithDuplicateLinesMergedThoseLines() {
        Order order = Order.place(CUSTOMER, sameLines());
        List<DomainEvent> events = order.pullDomainEvents();
        assertThat(order.lines().size()).isEqualTo(1);
        assertThat(events.size()).isEqualTo(1);
    }

    @Test
    void placingAnEmptyLinesOrderThrowsAnException() {
        AssertionsForClassTypes.assertThatThrownBy(() -> Order.place(CUSTOMER, List.of()))
            .isInstanceOf(IllegalStateException.class);
    }

    // Test helper: rebuild an Order with a known id and status, simulating
    // reconstruction from storage. Uses reflection only because our real
    // reconstruction constructor comes later (week 7, repositories).
    private static Order rebuild(OrderId id, OrderStatus status) {
        return Order.reconstitute(id, CUSTOMER, status, List.of());
    }
}