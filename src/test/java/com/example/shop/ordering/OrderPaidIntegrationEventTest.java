package com.example.shop.ordering;

import com.example.shop.agerestriction.domain.AgeRestriction;
import com.example.shop.ordering.application.OrderApplicationService;
import com.example.shop.ordering.application.PlaceOrderCommand;
import com.example.shop.ordering.domain.OrderId;
import com.example.shop.ordering.integration.OrderPaidIntegrationEvent;
import com.example.shop.shared.Country;
import com.example.shop.shared.Money;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Kata 1.5 — Domain events vs integration events; event-carried state transfer.
 *
 * <p>Paying an order publishes ordering's PUBLIC contract to the outside world: an
 * OrderPaidIntegrationEvent, distinct from the internal domain event OrderPaid. Foreign
 * contexts (e.g. warehouse) consume THIS, not ordering's internal model — so ordering can
 * evolve OrderPaid freely without breaking anyone. The integration event carries the state
 * a consumer needs, so it need not call back into ordering (event-carried state transfer).
 *
 * <p>This test is the acceptance criterion. Make it compile and pass by writing the
 * production code yourself. The published-language shape asserted here (String ids +
 * shared Money) is a reasonable default; if you deliberately choose a different contract,
 * adapt the field accessors below — but the integration event must NOT reference any type
 * from com.example.shop.ordering.domain.
 */
@SpringBootTest
@Import(OrderPaidIntegrationEventTest.CaptureIntegrationEvents.class)
class OrderPaidIntegrationEventTest {

    @Autowired OrderApplicationService orders;
    @Autowired CaptureIntegrationEvents captured;

    @Test
    void payingAnOrderPublishesAnOrderPaidIntegrationEventForOtherContexts() {
        String customerId = UUID.randomUUID().toString();

        // Order total = 2 x 50.00 PLN = 100.00 PLN
        OrderId orderId = orders.placeOrder(new PlaceOrderCommand(
            customerId,
            30,
            Country.of("PL"),
            List.of(new PlaceOrderCommand.Item(
                UUID.randomUUID().toString(),
                "Wino X", Money.of("50.00", "PLN"), 2,
                AgeRestriction.none()))
        ));

        orders.payOrder(orderId);

        // After the payment transaction COMMITS, ordering announces the fact to the world.
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            assertThat(captured.events).hasSize(1);
            OrderPaidIntegrationEvent event = captured.events.getFirst();
            assertThat(event.orderId()).isEqualTo(orderId.toString());
            assertThat(event.customerId()).isEqualTo(customerId);
            assertThat(event.amount()).isEqualTo("100.00");
            assertThat(event.currency()).isEqualTo("PLN");
        });
    }

    /** Test-only stand-in for a foreign context: captures the public integration event. */
    @TestConfiguration
    static class CaptureIntegrationEvents {
        final List<OrderPaidIntegrationEvent> events = new CopyOnWriteArrayList<>();

        @EventListener
        void on(OrderPaidIntegrationEvent event) {
            events.add(event);
        }
    }
}
