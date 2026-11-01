package com.example.shop.ordering.query;

import com.example.shop.agerestriction.domain.AgeRestriction;
import com.example.shop.ordering.application.AgeRestrictionViolation;
import com.example.shop.ordering.application.PayOrderCommand;
import com.example.shop.ordering.application.PayOrderCommandHandler;
import com.example.shop.ordering.application.PlaceOrderCommand;
import com.example.shop.ordering.application.PlaceOrderCommandHandler;
import com.example.shop.ordering.domain.CustomerId;
import com.example.shop.ordering.domain.OrderId;
import com.example.shop.ordering.domain.OrderStatus;
import com.example.shop.shared.Country;
import com.example.shop.shared.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Drives the system ONLY through command handlers and reads ONLY through OrderSummaryQuery.
 * Nobody calls save() on the store here — if the summary shows up, the projection built it.
 */
@SpringBootTest
class OrderSummaryProjectionTest {

    @Autowired PlaceOrderCommandHandler placeOrder;
    @Autowired PayOrderCommandHandler payOrder;
    @Autowired OrderSummaryQuery summaries;

    private final AgeRestriction alcohol = new AgeRestriction(Map.of(Country.of("PL"), 18));
    private final AgeRestriction none = new AgeRestriction(Map.of());

    private PlaceOrderCommand twoLineOrder(String customerId, int buyerAge) {
        return new PlaceOrderCommand(customerId, buyerAge, Country.of("PL"), List.of(
            new PlaceOrderCommand.Item(UUID.randomUUID().toString(), "Wino X", Money.of("50.00", "PLN"), 2, alcohol),
            new PlaceOrderCommand.Item(UUID.randomUUID().toString(), "Ser Y", Money.of("30.00", "PLN"), 1, none)
        ));
    }

    @Test
    void placingAnOrderProjectsAPlacedSummaryWithLinesAndTotal() {
        String customerId = UUID.randomUUID().toString();
        Instant before = Instant.now();

        OrderId id = placeOrder.handle(twoLineOrder(customerId, 30));

        OrderSummary summary = summaries.byId(id).orElseThrow();
        assertThat(summary.customerId()).isEqualTo(CustomerId.of(customerId));
        assertThat(summary.status()).isEqualTo(OrderStatus.PLACED);
        assertThat(summary.lineCount()).isEqualTo(2);
        assertThat(summary.total()).isEqualTo(Money.of("130.00", "PLN"));
        assertThat(summary.placedAt()).isBetween(before, Instant.now());
    }

    @Test
    void payingAnOrderFlipsStatusButNeverMovesPlacedAt() {
        OrderId id = placeOrder.handle(twoLineOrder(UUID.randomUUID().toString(), 30));
        OrderSummary placed = summaries.byId(id).orElseThrow();

        payOrder.handle(new PayOrderCommand(id.value().toString()));

        OrderSummary paid = summaries.byId(id).orElseThrow();
        assertThat(paid.status()).isEqualTo(OrderStatus.PAID);
        assertThat(paid.placedAt()).isEqualTo(placed.placedAt());
        assertThat(paid.lineCount()).isEqualTo(placed.lineCount());
        assertThat(paid.total()).isEqualTo(placed.total());
    }

    @Test
    void rejectedOrderLeavesNoSummary() {
        String customerId = UUID.randomUUID().toString();

        assertThatThrownBy(() -> placeOrder.handle(twoLineOrder(customerId, 17)))
            .isInstanceOf(AgeRestrictionViolation.class);

        assertThat(summaries.byCustomer(CustomerId.of(customerId))).isEmpty();
    }
}
