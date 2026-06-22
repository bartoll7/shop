package com.example.shop.ordering.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductIdTest {

    @Test
    void sameUnderlyingValueMeansEqual() {
        String raw = "123e4567-e89b-12d3-a456-426614174000";
        assertThat(ProductId.of(raw)).isEqualTo(ProductId.of(raw));
    }

    @Test
    void newIdsAreUnique() {
        assertThat(ProductId.newId()).isNotEqualTo(ProductId.newId());
    }

    @Test
    void nullValueIsRejected() {
        assertThatThrownBy(() -> new ProductId(null))
            .isInstanceOf(NullPointerException.class);
    }
}