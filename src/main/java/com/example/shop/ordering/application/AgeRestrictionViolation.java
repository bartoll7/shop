package com.example.shop.ordering.application;

/**
 * AgeRestrictionViolation — raised when a buyer is not allowed to purchase an
 * age-restricted product for the given shipping jurisdiction. A named business
 * exception, distinct from generic argument errors, so callers can handle it
 * specifically (e.g. map to an HTTP 403 with a clear message).
 */
public class AgeRestrictionViolation extends RuntimeException {

    public AgeRestrictionViolation(String message) {
        super(message);
    }
}