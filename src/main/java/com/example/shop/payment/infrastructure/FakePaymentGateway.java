package com.example.shop.payment.infrastructure;

import com.example.shop.payment.domain.PaymentGateway;
import com.example.shop.payment.domain.PaymentId;
import com.example.shop.shared.Money;
import org.springframework.stereotype.Component;

/**
 * FakePaymentGateway — ADAPTER for the outbound PaymentGateway port.
 *
 * <p>Translates OUR request (PaymentId, Money) into the provider's call (createCharge
 * with raw amount/currency strings). Carries our PaymentId as the merchant reference so
 * the later webhook can be correlated back to our payment.
 */
@Component
public class FakePaymentGateway implements PaymentGateway {

    private final FakeProviderApi provider;

    public FakePaymentGateway(FakeProviderApi provider) {
        this.provider = provider;
    }

    @Override
    public String initiatePayment(PaymentId paymentId, Money amount) {
        return provider.createCharge(
            amount.amount(),
            amount.currency().getCurrencyCode(),
            paymentId.toString()   // our id travels as the merchant order reference
        );
    }
}