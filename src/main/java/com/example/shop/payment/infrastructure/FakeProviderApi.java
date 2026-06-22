package com.example.shop.payment.infrastructure;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * FakeProviderApi — simulates an external payment provider (Stripe/PayU-like).
 *
 * <p>Speaks ITS OWN language: "charge", "reference", a status string. This vocabulary
 * is alien to our domain on purpose — it's exactly what the ACL must translate.
 *
 * <p>In-memory: initiating a charge immediately fires a "webhook" back into our app
 * (here, via Spring events carrying the provider's raw payload). A real provider would
 * POST JSON to an HTTP endpoint asynchronously.
 */
@Component
public class FakeProviderApi {

    private final ApplicationEventPublisher publisher;

    public FakeProviderApi(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /** The provider's raw webhook payload — THEIR shape, not ours. */
    public record ProviderWebhookPayload(String reference, String chargeStatus,
                                         BigDecimal amountValue, String currencyCode,
                                         String merchantOrderRef) {
    }

    /** Create a charge and (synchronously, for the demo) emit a success webhook. */
    public String createCharge(BigDecimal amount, String currency, String merchantOrderRef) {
        String reference = "ch_" + UUID.randomUUID();
        // Simulate the provider calling us back with a "succeeded" charge.
        publisher.publishEvent(new ProviderWebhookPayload(
            reference, "succeeded", amount, currency, merchantOrderRef));
        return reference;
    }
}