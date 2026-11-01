package com.example.shop.ordering.query;

/**
 * A upsert interface for persisting OrderSummary read models.
 */
public interface OrderSummaryUpsert {
    /**
     * Saves an OrderSummary read model object if no projection before, updates it if already present
     */
    void save(OrderSummary summary);
}
