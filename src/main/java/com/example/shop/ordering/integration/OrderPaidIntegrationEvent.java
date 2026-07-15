package com.example.shop.ordering.integration;

import com.example.shop.shared.IntegrationEvent;
import java.time.Instant;

public record OrderPaidIntegrationEvent(String orderId, String customerId, String amount, String currency, Instant occurredOn) implements
    IntegrationEvent {
}
