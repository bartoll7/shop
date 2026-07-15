package com.example.shop.shared;

/**
 * IntegrationEventPublisher — an output PORT for publishing integration events.
 *
 * <p>The application layer depends on this interface, not on Spring's
 * ApplicationEventPublisher. The Spring-based implementation is an adapter in
 * infrastructure, keeping the framework out of application/domain code.
 */
public interface IntegrationEventPublisher {
    void publish(IntegrationEvent event);
}