package com.example.shop.shared;

import java.time.Instant;

/**
 * IntegrationEvent — marker for things that have happened in application layer translated from domain event.
 *
 * <p>Events are immutable facts, named in the PAST tense, expressed in the stable public language
 * between contexts. {@code occurredOn} records when the fact happened.
 */
public interface IntegrationEvent {
    Instant occurredOn();
}