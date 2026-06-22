package com.example.shop.ordering.domain;

import com.example.shop.shared.Money;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderAggregateTest {

    private static final ProductId WINE = ProductId.newId();
    private static final ProductId CHEESE = ProductId.newId();

    @Test
    void addingLinesAccumulatesTheTotalFromSnapshots() {
        Order order = Order.place();
        order.addLine(WINE, "Wino X", Money.of("50.00", "PLN"), Quantity.of(2));
        order.addLine(CHEESE, "Ser Y", Money.of("20.00", "PLN"), Quantity.of(1));

        // 2 × 50 + 1 × 20 = 120
        assertThat(order.totalAmount()).isEqualTo(Money.of("120.00", "PLN"));
        assertThat(order.lines()).hasSize(2);
    }

    @Test
    void addingTheSameProductMergesQuantities() {
        Order order = Order.place();
        order.addLine(WINE, "Wino X", Money.of("50.00", "PLN"), Quantity.of(1));
        order.addLine(WINE, "Wino X", Money.of("50.00", "PLN"), Quantity.of(2));

        // Merged into one line of quantity 3, not two separate lines.
        assertThat(order.lines()).hasSize(1);
        assertThat(order.lines().getFirst().quantity()).isEqualTo(Quantity.of(3));
        assertThat(order.totalAmount()).isEqualTo(Money.of("150.00", "PLN"));
    }

    @Test
    void cannotMixCurrenciesAcrossLines() {
        Order order = Order.place();
        order.addLine(WINE, "Wino X", Money.of("50.00", "PLN"), Quantity.of(1));
        assertThatThrownBy(() ->
                               order.addLine(CHEESE, "Ser Y", Money.of("20.00", "EUR"), Quantity.of(1)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("currency");
    }

    @Test
    void cannotAddLinesToANonPlacedOrder() {
        Order order = Order.place();
        order.addLine(WINE, "Wino X", Money.of("50.00", "PLN"), Quantity.of(1));
        order.markAsPaid();
        assertThatThrownBy(() ->
                               order.addLine(CHEESE, "Ser Y", Money.of("20.00", "PLN"), Quantity.of(1)))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cannotPayAnOrderWithNoLines() {
        Order order = Order.place();
        assertThatThrownBy(order::markAsPaid)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("no lines");
    }

    @Test
    void exposedLinesCannotBeModifiedFromOutside() {
        Order order = Order.place();
        order.addLine(WINE, "Wino X", Money.of("50.00", "PLN"), Quantity.of(1));
        // The root must not be bypassable.
        assertThatThrownBy(() -> order.lines().add(
            new OrderLine(CHEESE, "Ser Y", Money.of("20.00", "PLN"), Quantity.of(1))))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}