package com.example.shop.loyalty;

import com.example.shop.loyalty.domain.CustomerId;
import com.example.shop.loyalty.infrastructure.OrderToAwardPointsTranslator;
import com.example.shop.ordering.integration.OrderPaidIntegrationEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Kata 1.7 — ACL on the loyalty side.
 *
 * <p>Loyalty must NOT depend on ordering's DOMAIN event. It consumes ordering's
 * PUBLISHED LANGUAGE (OrderPaidIntegrationEvent) and translates that foreign,
 * string-typed payload into loyalty's OWN terms via this ACL — exactly like
 * payment's ProviderWebhookTranslator does for the provider's webhook.
 */
class OrderToAwardPointsTranslatorTest {

    private final OrderToAwardPointsTranslator acl = new OrderToAwardPointsTranslator();

    @Test
    void translatesPublishedLanguageIntoLoyaltyTerms() {
        UUID customerUuid = UUID.randomUUID();
        OrderPaidIntegrationEvent published = new OrderPaidIntegrationEvent(
            UUID.randomUUID().toString(),
            customerUuid.toString(),
            "100.00",
            "PLN",
            Instant.now());

        OrderToAwardPointsTranslator.TranslatedOrder result = acl.translate(published);

        assertThat(result.customerId()).isEqualTo(CustomerId.of(customerUuid));
        assertThat(result.amount()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    void rejectsAnOrderInACurrencyLoyaltyDoesNotAccept() {
        OrderPaidIntegrationEvent foreignCurrency = new OrderPaidIntegrationEvent(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            "100.00",
            "EUR",
            Instant.now());

        assertThatThrownBy(() -> acl.translate(foreignCurrency))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("EUR");
    }

    @Test
    void rejectsAnAmountThatIsNotANumber() {
        OrderPaidIntegrationEvent notANumber = new OrderPaidIntegrationEvent(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            "not-a-number",
            "PLN",
            Instant.now());

        assertThatThrownBy(() -> acl.translate(notANumber))
            .isInstanceOf(NumberFormatException.class);
    }

    @Test
    void rejectsABlankCustomerId() {
        OrderPaidIntegrationEvent blankCustomer = new OrderPaidIntegrationEvent(
            UUID.randomUUID().toString(),
            "   ",
            "100.00",
            "PLN",
            Instant.now());

        assertThatThrownBy(() -> acl.translate(blankCustomer))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
