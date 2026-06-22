package com.example.shop.shared;

import java.time.Instant;

/**
 * DomainEvent — marker for things that have happened in the domain.
 *
 * <p>Events are immutable facts, named in the PAST tense, expressed in the language
 * of the business. {@code occurredOn} records when the fact happened.
 */
public interface DomainEvent {
    Instant occurredOn();
}