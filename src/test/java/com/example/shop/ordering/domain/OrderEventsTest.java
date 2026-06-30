package com.example.shop.ordering.domain;

import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.Money;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderEventsTest {
    private static final CustomerId CUSTOMER = CustomerId.newId();

    @Test
    void placingAnOrderRecordsOrderPlaced() {
        Order order = Order.place(CUSTOMER);
        assertThat(order.pullDomainEvents())
            .singleElement()
            .isInstanceOf(OrderPlaced.class);
    }

    @Test
    void payingAnOrderRecordsOrderPaidWithTheTotal() {
        Order order = Order.place(CUSTOMER);
        order.addLine(ProductId.newId(), "Wino X", Money.of("50.00", "PLN"), Quantity.of(2));
        order.pullDomainEvents(); // discard the OrderPlaced event for this test

        order.markAsPaid();

        List<DomainEvent> events = order.pullDomainEvents();
        assertThat(events).singleElement().isInstanceOf(OrderPaid.class);
        OrderPaid paid = (OrderPaid) events.getFirst();
        assertThat(paid.orderId()).isEqualTo(order.id());
        assertThat(paid.totalAmount()).isEqualTo(Money.of("100.00", "PLN"));
    }

    @Test
    void pullingEventsClearsThem() {
        Order order = Order.place(CUSTOMER);
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