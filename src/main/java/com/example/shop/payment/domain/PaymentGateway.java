package com.example.shop.payment.domain;

import com.example.shop.shared.Money;

/**
 * PaymentGateway — OUTPUT PORT (driven). The domain declares "I need to initiate a
 * payment with some external provider", without knowing which one or how. The adapter
 * lives in infrastructure and talks to the actual provider.
 *
 * <p>Note the language: our terms (PaymentId, Money), not the provider's. The provider's
 * vocabulary is confined to the adapter + ACL.
 */
public interface PaymentGateway {

    /** Ask the external provider to start a payment; returns the provider's reference. */
    String initiatePayment(PaymentId paymentId, Money amount);
}