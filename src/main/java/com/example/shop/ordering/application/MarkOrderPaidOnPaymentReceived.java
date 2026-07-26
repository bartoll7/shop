package com.example.shop.ordering.application;

import com.example.shop.payment.domain.PaymentReceived;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * MarkOrderPaidOnPaymentReceived — ordering reacts to the payment context.
 *
 * <p>Eventual consistency across contexts: when PaymentReceived arrives, ordering marks
 * the corresponding order as paid (which itself emits OrderPaid → warehouse reacts).
 * Ordering depends only on the clean PaymentReceived event — never on the provider.
 */
@Component
public class MarkOrderPaidOnPaymentReceived {

    private final PayOrderCommandHandler payOrderCommandHandler;

    public MarkOrderPaidOnPaymentReceived(PayOrderCommandHandler payOrderCommandHandler) {
        this.payOrderCommandHandler = payOrderCommandHandler;
    }

    @EventListener
    public void on(PaymentReceived event) {
        payOrderCommandHandler.handle(new PayOrderCommand(event.orderId().toString()));
    }
}