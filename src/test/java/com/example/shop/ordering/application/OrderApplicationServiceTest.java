package com.example.shop.ordering.application;

import com.example.shop.ordering.domain.OrderId;
import com.example.shop.ordering.domain.OrderPaid;
import com.example.shop.ordering.domain.OrderRepository;
import com.example.shop.shared.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(OrderApplicationServiceTest.TestEventCollector.class)
class OrderApplicationServiceTest {

    @Autowired
    OrderApplicationService service;

    @Autowired
    OrderRepository orders;

    @Autowired
    TestEventCollector collector;

    @Test
    void placingThenPayingAnOrderPersistsItAndPublishesOrderPaid() {
        var command = new PlaceOrderCommand(List.of(
            new PlaceOrderCommand.Item(
                UUID.randomUUID().toString(),
                "Wino X", Money.of("50.00", "PLN"), 2)
        ));

        OrderId id = service.placeOrder(command);
        service.payOrder(id);

        assertThat(orders.findById(id)).isPresent();
        assertThat(collector.paidEvents)
            .anySatisfy(e -> {
                assertThat(e.orderId()).isEqualTo(id);
                assertThat(e.totalAmount()).isEqualTo(Money.of("100.00", "PLN"));
            });
    }

    /**
     * Test-only listener. Declared as a static nested class but registered
     * EXPLICITLY via @Import, because component scanning does not pick up
     * components nested inside a test class.
     */
    static class TestEventCollector {
        final List<OrderPaid> paidEvents = new CopyOnWriteArrayList<>();

        @EventListener
        void on(OrderPaid event) {
            paidEvents.add(event);
        }
    }
}