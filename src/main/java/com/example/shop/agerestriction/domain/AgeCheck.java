package com.example.shop.agerestriction.domain;

import com.example.shop.shared.Country;

public record AgeCheck(int age, AgeRestriction restriction, Country shippingCountry) {
}
