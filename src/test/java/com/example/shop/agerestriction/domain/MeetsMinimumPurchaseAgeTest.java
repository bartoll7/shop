package com.example.shop.agerestriction.domain;

import com.example.shop.shared.Country;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MeetsMinimumPurchaseAgeTest {
    private final MeetsMinimumPurchaseAge meetsMinimumPurchaseAge = new MeetsMinimumPurchaseAge();

    private final AgeRestriction alcohol = new AgeRestriction(Map.of(
        Country.of("PL"), 18,
        Country.of("GB"), 18
    ));

    @Test
    void adultMayBuyRestrictedProduct() {
        var ageCheck = new AgeCheck(18, alcohol, Country.of("PL"));
        assertThat(meetsMinimumPurchaseAge.isSatisfiedBy(ageCheck)).isTrue();
    }

    @Test
    void adultMayNotBuyRestrictedProductWhenAgeRestrictionIsNotPresentInCountry() {
        var ageCheck = new AgeCheck(35, alcohol, Country.of("FR"));
        assertThat(meetsMinimumPurchaseAge.isSatisfiedBy(ageCheck)).isFalse();
    }

    @Test
    void minorMayNotBuyRestrictedProduct() {
        var ageCheck = new AgeCheck(17, alcohol, Country.of("PL"));
        assertThat(meetsMinimumPurchaseAge.isSatisfiedBy(ageCheck)).isFalse();
    }

    @Test
    void minorMayNotBuyProductWhenNoRestrictionInCountry() {
        var ageCheck = new AgeCheck(17, AgeRestriction.none(), Country.of("PL"));
        assertThat(meetsMinimumPurchaseAge.isSatisfiedBy(ageCheck)).isFalse();
    }
}
