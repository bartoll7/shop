package com.example.shop.ordering.application;

import com.example.shop.agerestriction.domain.PurchaseEligibility;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers domain services as beans WITHOUT polluting the domain with Spring
 * annotations. PurchaseEligibility stays a plain, framework-free domain service;
 * the wiring lives here, in the application layer.
 */
@Configuration
public class OrderingBeans {

    @Bean
    public PurchaseEligibility purchaseEligibility() {
        return new PurchaseEligibility();
    }
}