package com.example.shop.ordering.domain;

import com.example.shop.shared.Money;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void sameIdentityMeansEqual_evenWithDifferentState() {
        // The defining trait of an Entity: identity, not values, decides equality.
        Order order = Order.place();

        // Reconstruct a different object that shares the SAME id but is PAID.
        Order samePaid = rebuild(order.id(), OrderStatus.PAID);

        // Different status, but same identity -> the SAME order.
        assertThat(order).isEqualTo(samePaid);
    }

    @Test
    void differentIdentityMeansNotEqual_evenWithIdenticalState() {
        // Two freshly placed orders have identical state (both PLACED)...
        Order a = Order.place();
        Order b = Order.place();
        // ...but different identity -> different orders. Opposite of a Value Object.
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void aPlacedOrderCanBePaidThenShipped() {
        Order order = Order.place();
        order.addLine(ProductId.newId(), "Wino X", Money.of("50.00", "PLN"), Quantity.of(1));
        order.markAsPaid();
        order.markAsShipped();
        assertThat(order.status()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void cannotShipAnUnpaidOrder() {
        Order order = Order.place();
        assertThatThrownBy(order::markAsShipped)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("PAID");
    }

    @Test
    void cannotCancelAShippedOrder() {
        Order order = Order.place();
        order.addLine(ProductId.newId(), "Wino X", Money.of("50.00", "PLN"), Quantity.of(1));
        order.markAsPaid();
        order.markAsShipped();
        assertThatThrownBy(order::cancel)
            .isInstanceOf(IllegalStateException.class);
    }

    // Test helper: rebuild an Order with a known id and status, simulating
    // reconstruction from storage. Uses reflection only because our real
    // reconstruction constructor comes later (week 7, repositories).
    private static Order rebuild(OrderId id, OrderStatus status) {
        return Order.reconstitute(id, status, java.util.List.of());
    }
}