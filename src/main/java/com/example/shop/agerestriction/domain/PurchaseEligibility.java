package com.example.shop.agerestriction.domain;

import com.example.shop.shared.Country;
import com.example.shop.shared.Specification;

/**
 * PurchaseEligibility — a DOMAIN SERVICE.
 *
 * <p>Holds a rule that belongs to no single entity: it sits at the intersection of
 * the buyer (their age), the product (its AgeRestriction) and the jurisdiction
 * (the shipping country). None of those aggregates owns this rule, so it lives here.
 *
 * <p>Stateless, pure domain language, no infrastructure. It answers a domain
 * question ("may this buyer buy this product here?"), it does not orchestrate.
 */
public class PurchaseEligibility {

    /**
     * @param buyerAge        the buyer's age in years
     * @param restriction     the product's age restriction
     * @param shippingCountry the jurisdiction, determined by the shipping address
     * @return true if the purchase is allowed
     */
    public boolean isAllowed(int buyerAge, AgeRestriction restriction, Country shippingCountry) {
        var ageCheck = new AgeCheck(buyerAge, restriction, shippingCountry);
        Specification<AgeCheck> unrestrictedInJurisdiction = new UnrestrictedInJurisdiction();
        Specification<AgeCheck> meetsMinimumPurchaseAge = new MeetsMinimumPurchaseAge();
        Specification<AgeCheck> allowed = unrestrictedInJurisdiction.or(meetsMinimumPurchaseAge);

        return allowed.isSatisfiedBy(ageCheck);
    }
}