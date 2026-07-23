package com.example.shop.returns.domain;

import java.util.Optional;

/**
 * ReturnRepository — a PORT, owned by the domain.
 *
 * <p>This is NOT a DAO. It models a *collection of Return aggregates*: you save a
 * whole aggregate and you retrieve a whole aggregate. The domain
 * declares WHAT it needs to persist; it does not know HOW. Implementations live in
 * the infrastructure layer, so the dependency arrow points inward, toward the domain.
 *
 * <p>One aggregate, one repository.
 */
public interface ReturnRepository {

    /** Add a new aggregate or replace an existing one (by identity). */
    void save(Return aggregate);

    /** Retrieve a whole aggregate by its identity, if present. */
    Optional<Return> findById(ReturnId id);
}