package com.example.shop.ordering.infrastructure;

import com.example.shop.ordering.domain.Order;
import com.example.shop.ordering.domain.OrderId;
import com.example.shop.ordering.domain.OrderRepository;
import com.example.shop.ordering.domain.OrderStatus;
import com.example.shop.ordering.domain.ProductId;
import com.example.shop.ordering.domain.Quantity;
import com.example.shop.shared.Money;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryOrderRepositoryTest {

    private final OrderRepository orders = new InMemoryOrderRepository();

    @Test
    void savesAndRetrievesAWholeAggregateIncludingLines() {
        Order order = Order.place();
        order.addLine(ProductId.newId(), "Wino X", Money.of("50.00", "PLN"), Quantity.of(2));
        orders.save(order);

        Optional<Order> loaded = orders.findById(order.id());

        assertThat(loaded).isPresent();
        // Identity preserved (Entity equality is by id).
        assertThat(loaded.get()).isEqualTo(order);
        // The aggregate came back whole — lines and all.
        assertThat(loaded.get().lines()).hasSize(1);
        assertThat(loaded.get().totalAmount()).isEqualTo(Money.of("100.00", "PLN"));
    }

    @Test
    void findByUnknownIdReturnsEmpty() {
        assertThat(orders.findById(OrderId.newId())).isEmpty();
    }

    @Test
    void savingAgainReplacesTheStoredAggregate() {
        Order order = Order.place();
        order.addLine(ProductId.newId(), "Wino X", Money.of("50.00", "PLN"), Quantity.of(1));
        orders.save(order);

        order.markAsPaid();   // state change
        orders.save(order);   // save again under the same identity

        assertThat(orders.findById(order.id()))
            .get()
            .extracting(Order::status)
            .isEqualTo(OrderStatus.PAID);
    }

    @Test
    void retrievedAggregateIsIndependentOfTheStore() {
        Order order = Order.place();
        order.addLine(ProductId.newId(), "Wino X", Money.of("50.00", "PLN"), Quantity.of(1));
        orders.save(order);

        // Mutating the originally-saved object must NOT change what's stored,
        // because save captured a snapshot.
        order.markAsPaid();

        Optional<Order> loaded = orders.findById(order.id());
        assertThat(loaded).get().extracting(Order::status).isEqualTo(OrderStatus.PLACED);
    }
}