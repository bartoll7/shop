package com.example.shop.shared;

import java.util.List;

/**
 * DomainEventPublisher — an output PORT for publishing domain events.
 *
 * <p>The application layer depends on this interface, not on Spring's
 * ApplicationEventPublisher. The Spring-based implementation is an adapter in
 * infrastructure, keeping the framework out of application/domain code.
 */
public interface DomainEventPublisher {
    void publish(DomainEvent event);

    default void publishAll(List<DomainEvent> domainEvents) {
        for (DomainEvent event : domainEvents) {
            publish(event);
        }
    }
}