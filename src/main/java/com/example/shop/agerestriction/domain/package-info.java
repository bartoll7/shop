/**
 * AGE RESTRICTION — domain layer (SUPPORTING subdomain).
 *
 * <p>Decides whether a buyer may purchase an age-restricted product, based on
 * the jurisdiction determined by the SHIPPING COUNTRY (decision recorded in
 * week 1 with "legal" — not country of citizenship, not IP).
 *
 * <p>Ubiquitous Language: AgeRestriction, MinimumPurchaseAge, ShippingCountry,
 * Jurisdiction. Done "properly but frugally" — it must work, it is not our
 * competitive edge.
 */
package com.example.shop.agerestriction.domain;
