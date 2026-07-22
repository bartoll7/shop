package com.example.shop.loyalty.application;

import com.example.shop.loyalty.infrastructure.OrderToAwardPointsTranslator;
import com.example.shop.ordering.integration.OrderPaidIntegrationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AwardPointsOnOrderPaid {

    private final AwardPointsApplicationService awardPointsApplicationService;
    private final OrderToAwardPointsTranslator orderToAwardPointsTranslator;

    public AwardPointsOnOrderPaid(AwardPointsApplicationService awardPointsApplicationService, OrderToAwardPointsTranslator orderToAwardPointsTranslator) {
        this.awardPointsApplicationService = awardPointsApplicationService;
        this.orderToAwardPointsTranslator = orderToAwardPointsTranslator;
    }

    @EventListener
    public void on(OrderPaidIntegrationEvent event) {
        OrderToAwardPointsTranslator.TranslatedOrder translatedOrder = orderToAwardPointsTranslator.translate(event);

        awardPointsApplicationService.awardCustomer(translatedOrder.customerId().value(), translatedOrder.amount());
    }
}