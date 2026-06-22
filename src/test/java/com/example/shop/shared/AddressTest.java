package com.example.shop.shared;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddressTest {

    private Address sampleAddress(String city) {
        return new Address("Marszałkowska", "1", "00-001", city, Country.of("PL"));
    }

    @Test
    void twoAddressesWithSameValuesAreEqual() {
        assertThat(sampleAddress("Warszawa")).isEqualTo(sampleAddress("Warszawa"));
    }

    @Test
    void differentCityMeansDifferentAddress() {
        // Changing a field yields a *different* address — there is no identity.
        assertThat(sampleAddress("Warszawa")).isNotEqualTo(sampleAddress("Kraków"));
    }

    @Test
    void blankCityIsRejected() {
        assertThatThrownBy(() -> sampleAddress("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("city");
    }

    @Test
    void countryCodeIsNormalized() {
        Country lower = Country.of(" pl ");
        assertThat(lower).isEqualTo(Country.of("PL"));
    }

    @Test
    void unknownCountryIsRejected() {
        assertThatThrownBy(() -> Country.of("ZZ"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}