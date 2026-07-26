package com.example.shop.ordering.application;

import com.example.shop.agerestriction.domain.AgeRestriction;
import com.example.shop.ordering.domain.OrderId;
import com.example.shop.ordering.domain.OrderPlaced;
import com.example.shop.ordering.domain.OrderRepository;
import com.example.shop.shared.Country;
import com.example.shop.shared.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(PlaceOrderCommandHandlerTest.TestEventCollector.class)
class PlaceOrderCommandHandlerTest {

    @Autowired
    PlaceOrderCommandHandler handler;

    @Autowired
    OrderRepository orders;

    @Autowired
    TestEventCollector collector;

    private final AgeRestriction alcohol = new AgeRestriction(Map.of(
        Country.of("PL"), 18
    ));

    private PlaceOrderCommand command(int buyerAge) {
        return new PlaceOrderCommand(
            UUID.randomUUID().toString(),
            buyerAge,
            Country.of("PL"),
            List.of(new PlaceOrderCommand.Item(
                UUID.randomUUID().toString(),
                "Wino X", Money.of("50.00", "PLN"), 2, alcohol))
        );
    }

    @Test
    void handleReturnsOnlyTheOrderIdAndPersistsTheAggregate() {
        OrderId id = handler.handle(command(18));

        assertThat(id).isNotNull();
        assertThat(orders.findById(id)).isPresent();
        assertThat(collector.placedEvents)
            .anySatisfy(e -> {
                assertThat(e.orderId()).isEqualTo(id);
            });
    }

    @Test
    void underageBuyerIsRejectedAndNothingIsPublished() {
        collector.placedEvents.clear();

        assertThatThrownBy(() -> handler.handle(command(17)))
            .isInstanceOf(AgeRestrictionViolation.class)
            .hasMessageContaining("Wino X");

        assertThat(collector.placedEvents).isEmpty();
    }

    static class TestEventCollector {
        final List<OrderPlaced> placedEvents = new CopyOnWriteArrayList<>();

        @EventListener
        void on(OrderPlaced event) {
            placedEvents.add(event);
        }
    }
}
