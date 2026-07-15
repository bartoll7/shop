package com.example.shop.warehouse;

import com.example.shop.ordering.integration.OrderPaidIntegrationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * StockOnOrderPaid — a listener in the (future) warehouse context.
 *
 * <p>Demonstrates EVENTUAL CONSISTENCY across aggregates/contexts: the warehouse
 * reacts to OrderPaid on its own, in a separate step, WITHOUT Order knowing the
 * warehouse exists. Today it only logs; later it would reserve/release stock for
 * the products in that order (fetched by id — reference by id, as we practiced).
 */
@Component
public class StockOnOrderPaid {

    @EventListener
    public void on(OrderPaidIntegrationEvent event) {
        System.out.printf("[warehouse] Order %s was paid total: %s %s. Would adjust stock now.%n", event.orderId(), event.amount(), event.currency());
    }
}