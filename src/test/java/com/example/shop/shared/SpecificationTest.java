package com.example.shop.shared;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Drives the generic Specification building block: a business rule reified as an
 * object, composable with and/or/not. These tests use trivial number rules on
 * purpose, so the COMPOSITION operators are exercised in isolation from any single
 * domain rule.
 */
class SpecificationTest {

    private static final Specification<Integer> POSITIVE = n -> n > 0;
    private static final Specification<Integer> EVEN = n -> n % 2 == 0;

    @Test
    void isSatisfiedBy_evaluatesTheRule() {
        assertThat(POSITIVE.isSatisfiedBy(1)).isTrue();
        assertThat(POSITIVE.isSatisfiedBy(-1)).isFalse();
    }

    @Test
    void and_requiresBothToHold() {
        Specification<Integer> positiveAndEven = POSITIVE.and(EVEN);

        assertThat(positiveAndEven.isSatisfiedBy(4)).isTrue();
        assertThat(positiveAndEven.isSatisfiedBy(3)).isFalse();   // positive, not even
        assertThat(positiveAndEven.isSatisfiedBy(-2)).isFalse();  // even, not positive
    }

    @Test
    void or_requiresEitherToHold() {
        Specification<Integer> positiveOrEven = POSITIVE.or(EVEN);

        assertThat(positiveOrEven.isSatisfiedBy(3)).isTrue();    // positive
        assertThat(positiveOrEven.isSatisfiedBy(-2)).isTrue();   // even
        assertThat(positiveOrEven.isSatisfiedBy(-1)).isFalse();  // neither
    }

    @Test
    void not_negatesTheRule() {
        Specification<Integer> notPositive = POSITIVE.not();

        assertThat(notPositive.isSatisfiedBy(-1)).isTrue();
        assertThat(notPositive.isSatisfiedBy(1)).isFalse();
    }

    @Test
    void operatorsComposeAndNest() {
        // "positive and even, or exactly -7" — proves operators chain and nest.
        Specification<Integer> minusSeven = n -> n == -7;
        Specification<Integer> rule = POSITIVE.and(EVEN).or(minusSeven);

        assertThat(rule.isSatisfiedBy(4)).isTrue();
        assertThat(rule.isSatisfiedBy(-7)).isTrue();
        assertThat(rule.isSatisfiedBy(3)).isFalse();
    }
}
