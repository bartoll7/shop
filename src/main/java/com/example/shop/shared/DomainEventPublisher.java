package com.example.shop.shared;

/**
 * DomainEventPublisher — an output PORT for publishing domain events.
 *
 * <p>The application layer depends on this interface, not on Spring's
 * ApplicationEventPublisher. The Spring-based implementation is an adapter in
 * infrastructure, keeping the framework out of application/domain code.
 */
public interface DomainEventPublisher {
    void publish(DomainEvent event);
}