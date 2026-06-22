package com.example.shop.agerestriction.domain;

import com.example.shop.shared.Country;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseEligibilityTest {

    private final PurchaseEligibility eligibility = new PurchaseEligibility();

    // Alcohol: 18 in both PL and GB (the corrected week-1 fact; "UK = 21" was wrong).
    private final AgeRestriction alcohol = new AgeRestriction(Map.of(
        Country.of("PL"), 18,
        Country.of("GB"), 18
    ));

    @Test
    void adultMayBuyRestrictedProduct() {
        assertThat(eligibility.isAllowed(18, alcohol, Country.of("PL"))).isTrue();
    }

    @Test
    void underageMayNotBuyRestrictedProduct() {
        assertThat(eligibility.isAllowed(17, alcohol, Country.of("PL"))).isFalse();
    }

    @Test
    void noRestrictionForJurisdictionMeansAllowed() {
        // A product restricted in PL/GB but shipped to a country not in the map.
        assertThat(eligibility.isAllowed(15, alcohol, Country.of("FR"))).isTrue();
    }

    @Test
    void unrestrictedProductIsAlwaysAllowed() {
        assertThat(eligibility.isAllowed(12, AgeRestriction.none(), Country.of("PL"))).isTrue();
    }
}