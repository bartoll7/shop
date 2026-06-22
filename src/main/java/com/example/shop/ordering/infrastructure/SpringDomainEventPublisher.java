package com.example.shop.ordering.infrastructure;

import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * SpringDomainEventPublisher — adapter implementing the DomainEventPublisher port
 * using Spring's in-process ApplicationEventPublisher. The framework dependency is
 * confined HERE; application and domain code never see it.
 */
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher springPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher springPublisher) {
        this.springPublisher = springPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        springPublisher.publishEvent(event);
    }
}