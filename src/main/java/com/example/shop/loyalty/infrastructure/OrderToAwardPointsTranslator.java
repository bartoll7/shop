package com.example.shop.loyalty.infrastructure;

import com.example.shop.loyalty.domain.CustomerId;
import com.example.shop.ordering.integration.OrderPaidIntegrationEvent;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class OrderToAwardPointsTranslator {

    /** Loyalty's own rule: points are only awarded for orders paid in PLN. */
    private static final String ACCEPTED_CURRENCY = "PLN";

    public TranslatedOrder translate(OrderPaidIntegrationEvent event) {
        if (!ACCEPTED_CURRENCY.equals(event.currency())) {
            throw new IllegalArgumentException(
                "Loyalty only awards points for " + ACCEPTED_CURRENCY + " orders, got: " + event.currency());
        }
        CustomerId customerId = CustomerId.of(UUID.fromString(event.customerId()));
        BigDecimal amount = new BigDecimal(event.amount());

        return new TranslatedOrder(customerId, amount);
    }

    public record TranslatedOrder(CustomerId customerId, BigDecimal amount) {}
}

