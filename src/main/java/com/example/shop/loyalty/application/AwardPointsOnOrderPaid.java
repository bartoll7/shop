package com.example.shop.loyalty.application;

import com.example.shop.ordering.domain.OrderPaid;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AwardPointsOnOrderPaid {

    private final AwardPointsApplicationService awardPointsApplicationService;

    public AwardPointsOnOrderPaid(AwardPointsApplicationService awardPointsApplicationService) {
        this.awardPointsApplicationService = awardPointsApplicationService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrderPaid event) {
        awardPointsApplicationService.awardCustomer(event.customerId().value(), event.totalAmount().amount());
    }
}