package com.example.shop.ordering.application;

import com.example.shop.ordering.domain.OrderPaid;
import com.example.shop.ordering.integration.OrderPaidIntegrationEvent;
import com.example.shop.shared.IntegrationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderPaidToIntegrationEventTranslator {

    private final IntegrationEventPublisher integrationEventPublisher;

    public OrderPaidToIntegrationEventTranslator(IntegrationEventPublisher integrationEventPublisher) {
        this.integrationEventPublisher = integrationEventPublisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrderPaid event) {
        integrationEventPublisher.publish(
            new OrderPaidIntegrationEvent(
                event.orderId().value().toString(),
                event.customerId().value().toString(),
                event.totalAmount().amount().toString(),
                event.totalAmount().currency().getCurrencyCode(),
                event.occurredOn()
            )
        );
    }
}