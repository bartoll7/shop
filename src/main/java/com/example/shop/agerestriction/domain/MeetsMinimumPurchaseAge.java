package com.example.shop.agerestriction.domain;

import com.example.shop.shared.Specification;

public class MeetsMinimumPurchaseAge implements Specification<AgeCheck> {
    @Override
    public boolean isSatisfiedBy(AgeCheck candidate) {
        return candidate.restriction().minimumAgeFor(candidate.shippingCountry())
            .map(minAge -> candidate.age() >= minAge).orElse(false);
    }
}
