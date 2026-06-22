package com.example.shop.payment.application;

import com.example.shop.ordering.domain.OrderId;
import com.example.shop.payment.domain.Payment;
import com.example.shop.payment.domain.PaymentGateway;
import com.example.shop.payment.domain.PaymentId;
import com.example.shop.payment.domain.PaymentRepository;
import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.DomainEventPublisher;
import com.example.shop.shared.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * PaymentApplicationService — orchestrates payment use cases.
 *
 * <p>initiate: create the Payment aggregate, persist, then ask the external provider
 * via the PaymentGateway PORT. confirmPayment: driven by the webhook adapter (after the
 * ACL has translated the provider's payload), it transitions the aggregate and publishes
 * our PaymentReceived event — only after a durable save.
 *
 * <p>Depends on PORTS only (repository, gateway, event publisher). No provider specifics.
 */
@Service
public class PaymentApplicationService {

    private final PaymentRepository payments;
    private final PaymentGateway gateway;
    private final DomainEventPublisher events;

    public PaymentApplicationService(PaymentRepository payments,
        PaymentGateway gateway,
        DomainEventPublisher events) {
        this.payments = payments;
        this.gateway = gateway;
        this.events = events;
    }

    @Transactional
    public PaymentId initiatePayment(OrderId orderId, Money amount) {
        Payment payment = Payment.initiate(orderId, amount);
        payments.save(payment);
        // Outbound: ask the provider to start the charge (may trigger the webhook).
        gateway.initiatePayment(payment.id(), amount);
        return payment.id();
    }

    @Transactional
    public void confirmPayment(PaymentId paymentId, boolean succeeded) {
        Payment payment = payments.findById(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown payment: " + paymentId));
        if (succeeded) {
            payment.markSucceeded();
        } else {
            payment.markFailed();
        }
        payments.save(payment);
        publishEvents(payment.pullDomainEvents());
    }

    private void publishEvents(List<DomainEvent> domainEvents) {
        for (DomainEvent event : domainEvents) {
            events.publish(event);
        }
    }
}