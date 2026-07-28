package com.example.shop.ordering.query;

import com.example.shop.ordering.domain.CustomerId;
import com.example.shop.ordering.domain.OrderId;
import com.example.shop.ordering.domain.OrderStatus;
import com.example.shop.ordering.infrastructure.InMemoryOrderSummaryStore;
import com.example.shop.shared.Money;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Drives the read model: a SEPARATE model with its own store, queried without ever
 * touching the Order aggregate or OrderRepository.
 *
 * <p>Nothing in this file mentions Order, addLine or OrderRepository. That is the
 * lesson, not an omission: the read side is fed rows, it does not reconstruct
 * aggregates and it does no arithmetic at query time.
 */
class OrderSummaryQueryTest {

    private final InMemoryOrderSummaryStore store = new InMemoryOrderSummaryStore();
    private final OrderSummaryQuery query = store;

    @Test
    void returnsTheStoredSummaryForAKnownOrder() {
        OrderId orderId = OrderId.newId();
        CustomerId customer = CustomerId.newId();
        Instant placedAt = Instant.parse("2026-07-28T10:15:30Z");

        store.save(new OrderSummary(orderId, customer, OrderStatus.PAID, 3, Money.of("78.00", "PLN"), placedAt));

        Optional<OrderSummary> found = query.byId(orderId);

        assertThat(found).contains(
            new OrderSummary(orderId, customer, OrderStatus.PAID, 3, Money.of("78.00", "PLN"), placedAt));
    }

    @Test
    void listsSummariesOfOneCustomerNewestFirst() {
        CustomerId customer = CustomerId.newId();
        CustomerId otherCustomer = CustomerId.newId();

        OrderSummary older = new OrderSummary(OrderId.newId(), customer, OrderStatus.PAID, 1,
            Money.of("10.00", "PLN"), Instant.parse("2026-07-20T08:00:00Z"));
        OrderSummary newer = new OrderSummary(OrderId.newId(), customer, OrderStatus.PLACED, 2,
            Money.of("30.00", "PLN"), Instant.parse("2026-07-27T08:00:00Z"));
        OrderSummary someoneElses = new OrderSummary(OrderId.newId(), otherCustomer, OrderStatus.PLACED, 1,
            Money.of("99.00", "PLN"), Instant.parse("2026-07-28T08:00:00Z"));

        // Saved oldest-first on purpose: insertion order must NOT be what decides the
        // result order. The read model owns its ordering contract explicitly.
        store.save(older);
        store.save(newer);
        store.save(someoneElses);

        List<OrderSummary> summaries = query.byCustomer(customer);

        assertThat(summaries).containsExactly(newer, older);
    }

    @Test
    void returnsEmptyForAnUnknownOrder() {
        assertThat(query.byId(OrderId.newId())).isEmpty();
    }

    @Test
    void returnsNoSummariesForACustomerWithoutOrders() {
        assertThat(query.byCustomer(CustomerId.newId())).isEmpty();
    }

    @Test
    void savingSummaryForTheSameOrderIdWithDifferentDataUpdatesIt() {
        OrderId orderId = OrderId.newId();
        CustomerId customer = CustomerId.newId();

        store.save(new OrderSummary(orderId, customer, OrderStatus.PLACED, 2, Money.of("50.00", "PLN"), Instant.parse("2026-07-28T10:15:30Z")));

        OrderSummary updatedSummary = new OrderSummary(orderId, customer, OrderStatus.PAID, 2, Money.of("50.00", "PLN"), Instant.parse("2026-07-28T10:20:00Z"));
        store.save(updatedSummary);

        Optional<OrderSummary> found = query.byId(orderId);

        assertThat(found).contains(updatedSummary);
    }
}
