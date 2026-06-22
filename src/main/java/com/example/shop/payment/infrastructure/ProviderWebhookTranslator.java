package com.example.shop.payment.infrastructure;

import com.example.shop.payment.domain.PaymentId;
import com.example.shop.shared.Money;
import org.springframework.stereotype.Component;

/**
 * ProviderWebhookTranslator — the ANTICORRUPTION LAYER.
 *
 * <p>Its single job: turn the provider's foreign payload into OUR concepts. Nothing
 * provider-specific ("charge", "succeeded", string amounts) is allowed past this point.
 * Swap providers later → rewrite only this class. The domain never changes.
 */
@Component
public class ProviderWebhookTranslator {

    /** Our clean, internal interpretation of a webhook. */
    public record TranslatedPayment(PaymentId paymentId, Money amount, boolean succeeded) {
    }

    public TranslatedPayment translate(FakeProviderApi.ProviderWebhookPayload payload) {
        // Provider's merchant reference is our PaymentId.
        PaymentId paymentId = PaymentId.of(payload.merchantOrderRef());
        // Provider's amount/currency strings become our Money VO (with its invariants).
        Money amount = Money.of(payload.amountValue(), payload.currencyCode());
        // Provider's status string becomes a clean boolean in our terms.
        boolean succeeded = "succeeded".equalsIgnoreCase(payload.chargeStatus());
        return new TranslatedPayment(paymentId, amount, succeeded);
    }
}