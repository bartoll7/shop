package com.example.shop.agerestriction.domain;

import com.example.shop.shared.Specification;

public class UnrestrictedInJurisdiction implements Specification<AgeCheck> {
    @Override
    public boolean isSatisfiedBy(AgeCheck candidate) {
        return candidate.restriction().minimumAgeFor(candidate.shippingCountry()).isEmpty();
    }
}
