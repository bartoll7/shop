package com.example.shop.agerestriction.domain;

import com.example.shop.shared.Country;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UnrestrictedInJurisdictionTest {
    private final UnrestrictedInJurisdiction unrestrictedInJurisdiction = new UnrestrictedInJurisdiction();

    private final AgeRestriction alcohol = new AgeRestriction(Map.of(
        Country.of("PL"), 18,
        Country.of("GB"), 18
    ));

    @Test
    void adultMayBuyRestrictedProductWhenAgeRestrictionIsNotPresentInCountry() {
        var ageCheck = new AgeCheck(18, alcohol, Country.of("FR"));
        assertThat(unrestrictedInJurisdiction.isSatisfiedBy(ageCheck)).isTrue();
    }

    @Test
    void minorMayBuyRestrictedProductWhenAgeRestrictionIsNotPresentInCountry() {
        var ageCheck = new AgeCheck(16, alcohol, Country.of("FR"));
        assertThat(unrestrictedInJurisdiction.isSatisfiedBy(ageCheck)).isTrue();
    }

    @Test
    void whetherThereAreRestrictionsOnAlcoholInAGivenCountry() {
        var ageCheck = new AgeCheck(98, alcohol, Country.of("PL"));
        assertThat(unrestrictedInJurisdiction.isSatisfiedBy(ageCheck)).isFalse();
    }
}
