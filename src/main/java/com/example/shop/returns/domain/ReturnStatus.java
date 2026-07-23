package com.example.shop.returns.domain;

/**
 * ReturnStatus — the lifecycle states of a Return, in the language of the business.
 */
public enum ReturnStatus {
    REQUESTED,
    SENT,
    DELIVERED,
    ACCEPTED,
    REJECTED
}