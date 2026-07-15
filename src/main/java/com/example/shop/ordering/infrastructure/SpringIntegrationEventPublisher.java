package com.example.shop.ordering.infrastructure;

import com.example.shop.shared.IntegrationEvent;
import com.example.shop.shared.IntegrationEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * SpringIntegrationEventPublisher — adapter implementing the IntegrationEventPublisher port
 * using Spring's in-process ApplicationEventPublisher. The framework dependency is
 * confined HERE; application and domain code never see it.
 */
@Component
public class SpringIntegrationEventPublisher implements IntegrationEventPublisher {

    private final ApplicationEventPublisher springPublisher;

    public SpringIntegrationEventPublisher(ApplicationEventPublisher springPublisher) {
        this.springPublisher = springPublisher;
    }

    @Override
    public void publish(IntegrationEvent event) {
        springPublisher.publishEvent(event);
    }
}