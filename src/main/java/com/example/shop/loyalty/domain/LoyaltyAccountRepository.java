package com.example.shop.loyalty.domain;

import java.util.Optional;

public interface LoyaltyAccountRepository {

    /** Add a new aggregate or replace an existing one (by identity). */
    void save(LoyaltyAccount loyaltyAccount);

    /** Retrieve a whole aggregate by its identity, if present. */
    Optional<LoyaltyAccount> findByCustomer(CustomerId id);
}