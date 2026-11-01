package com.example.shop.ordering.domain;

import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.Money;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderEventsTest {
    private static final CustomerId CUSTOMER = CustomerId.newId();

    private static Order placedOrder() {
        return Order.place(CUSTOMER, List.of(
            new OrderLine(ProductId.newId(), "Wino X", Money.of("50.00", "PLN"), Quantity.of(2))));
    }

    @Test
    void placingAnOrderRecordsOrderPlaced() {
        Order order = placedOrder();
        assertThat(order.pullDomainEvents())
            .singleElement()
            .isInstanceOf(OrderPlaced.class);
    }

    @Test
    void payingAnOrderRecordsOrderPaidWithTheTotal() {
        Order order = placedOrder();
        order.pullDomainEvents(); // discard the OrderPlaced event for this test

        order.markAsPaid();

        List<DomainEvent> events = order.pullDomainEvents();
        assertThat(events).singleElement().isInstanceOf(OrderPaid.class);
        OrderPaid paid = (OrderPaid) events.getFirst();
        assertThat(paid.orderId()).isEqualTo(order.id());
        assertThat(paid.totalAmount()).isEqualTo(Money.of("100.00", "PLN"));
    }

    @Test
    void placingAnOrderWithTwoOrderLinesEmitsSingleOrderPlacedEvent() {
        Order order = Order.place(CUSTOMER, List.of(
            new OrderLine(ProductId.newId(), "Wino X", Money.of("50.00", "PLN"), Quantity.of(2)),
            new OrderLine(ProductId.newId(), "Wino Y", Money.of("30.00", "PLN"), Quantity.of(1))));

        List<DomainEvent> events = order.pullDomainEvents();
        assertThat(events).singleElement().isInstanceOf(OrderPlaced.class);
        assertThat(events.size()).isEqualTo(1);
    }

    @Test
    void pullingEventsClearsThem() {
        Order order = placedOrder();
        order.pullDomainEvents();                       // first pull returns OrderPlaced
        assertThat(order.pullDomainEvents()).isEmpty(); // second pull is empty
    }

    @Test
    void reconstitutedOrderHasNoEvents() {
        // Loading from storage must NOT produce events.
        Order order = Order.reconstitute(OrderId.newId(), CUSTOMER, OrderStatus.PAID, List.of());
        assertThat(order.pullDomainEvents()).isEmpty();
    }
}