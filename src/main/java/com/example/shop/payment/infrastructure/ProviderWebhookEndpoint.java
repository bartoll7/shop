package com.example.shop.payment.infrastructure;

import com.example.shop.payment.application.PaymentApplicationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * ProviderWebhookEndpoint — INPUT (driving) ADAPTER for the inbound webhook.
 *
 * <p>In production this would be an @RestController @PostMapping receiving raw JSON,
 * verifying the signature, etc. Here it listens for the fake provider's payload event.
 * It hands the foreign payload to the ACL, then drives the application service with the
 * clean result. No provider vocabulary leaks beyond the ACL call.
 */
@Component
public class ProviderWebhookEndpoint {

    private final ProviderWebhookTranslator acl;
    private final PaymentApplicationService paymentService;

    public ProviderWebhookEndpoint(ProviderWebhookTranslator acl,
        PaymentApplicationService paymentService) {
        this.acl = acl;
        this.paymentService = paymentService;
    }

    @EventListener
    public void onProviderWebhook(FakeProviderApi.ProviderWebhookPayload payload) {
        ProviderWebhookTranslator.TranslatedPayment translated = acl.translate(payload);
        paymentService.confirmPayment(translated.paymentId(), translated.succeeded());
    }
}