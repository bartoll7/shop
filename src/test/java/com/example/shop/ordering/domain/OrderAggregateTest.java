package com.example.shop.ordering.domain;

import com.example.shop.shared.Money;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderAggregateTest {

    private static final ProductId WINE = ProductId.newId();
    private static final ProductId CHEESE = ProductId.newId();
    private static final CustomerId CUSTOMER = CustomerId.newId();

    private static OrderLine line(ProductId product, String name, String price, String currency, int qty) {
        return new OrderLine(product, name, Money.of(price, currency), Quantity.of(qty));
    }

    @Test
    void placingAccumulatesTheTotalFromSnapshots() {
        Order order = Order.place(CUSTOMER, List.of(
            line(WINE, "Wino X", "50.00", "PLN", 2),
            line(CHEESE, "Ser Y", "20.00", "PLN", 1)));

        // 2 × 50 + 1 × 20 = 120
        assertThat(order.totalAmount()).isEqualTo(Money.of("120.00", "PLN"));
        assertThat(order.lines()).hasSize(2);
    }

    @Test
    void placingTheSameProductTwiceMergesQuantities() {
        Order order = Order.place(CUSTOMER, List.of(
            line(WINE, "Wino X", "50.00", "PLN", 1),
            line(WINE, "Wino X", "50.00", "PLN", 2)));

        // Merged into one line of quantity 3, not two separate lines.
        assertThat(order.lines()).hasSize(1);
        assertThat(order.lines().getFirst().quantity()).isEqualTo(Quantity.of(3));
        assertThat(order.totalAmount()).isEqualTo(Money.of("150.00", "PLN"));
    }

    @Test
    void cannotMixCurrenciesAcrossLines() {
        assertThatThrownBy(() -> Order.place(CUSTOMER, List.of(
            line(WINE, "Wino X", "50.00", "PLN", 1),
            line(CHEESE, "Ser Y", "20.00", "EUR", 1))))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("currency");
    }

    @Test
    void cannotPayAnOrderWithNoLines() {
        // place() can no longer produce an empty order, so reconstitute one.
        Order order = Order.reconstitute(OrderId.newId(), CUSTOMER, OrderStatus.PLACED, List.of());
        assertThatThrownBy(order::markAsPaid)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("no lines");
    }

    @Test
    void exposedLinesCannotBeModifiedFromOutside() {
        Order order = Order.place(CUSTOMER, List.of(line(WINE, "Wino X", "50.00", "PLN", 1)));
        // The root must not be bypassable.
        assertThatThrownBy(() -> order.lines().add(line(CHEESE, "Ser Y", "20.00", "PLN", 1)))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
