package com.example.shop.ordering.application;

import com.example.shop.ordering.domain.*;
import com.example.shop.shared.Money;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(PayOrderCommandHandlerTest.TestEventCollector.class)
class PayOrderCommandHandlerTest {

    @Autowired
    PayOrderCommandHandler payOrderCommandHandler;

    @Autowired
    OrderRepository orders;

    @Autowired
    TestEventCollector collector;

    private PayOrderCommand command() {
        return new PayOrderCommand(
            UUID.randomUUID().toString()
        );
    }

    /**
     * This test is the acceptance criteria — the order must be persisted and OrderPaid must be published.
     */
    @Test
    void placingThenPayingAnOrderPersistsItAndPublishesOrderPaid() {
        Order order = Order.place(CustomerId.newId());
        OrderId id = order.id();
        order.addLine(ProductId.newId(), "Wino X", Money.of("50.00", "PLN"), Quantity.of(2));

        orders.save(order);
        payOrderCommandHandler.handle(new PayOrderCommand(order.id().toString()));

        assertThat(id).isNotNull();
        assertThat(orders.findById(id)).isPresent();
        assertThat(collector.paidEvents)
            .anySatisfy(e -> {
                assertThat(e.orderId()).isEqualTo(id);
                assertThat(e.totalAmount()).isEqualTo(Money.of("100.00", "PLN"));
            });
    }

    @Test
    void unableToPayForNotExistingOrder() {
        collector.paidEvents.clear();

        assertThatThrownBy(() -> payOrderCommandHandler.handle(command()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unknown order");

        assertThat(collector.paidEvents).isEmpty();
    }

    static class TestEventCollector {
        final List<OrderPaid> paidEvents = new CopyOnWriteArrayList<>();

        @EventListener
        void on(OrderPaid event) {
            paidEvents.add(event);
        }
    }
}
