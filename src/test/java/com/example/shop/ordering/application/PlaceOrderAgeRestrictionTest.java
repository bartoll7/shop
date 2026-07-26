package com.example.shop.ordering.application;

import com.example.shop.agerestriction.domain.AgeRestriction;
import com.example.shop.ordering.domain.OrderRepository;
import com.example.shop.shared.Country;
import com.example.shop.shared.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PlaceOrderAgeRestrictionTest {

    @Autowired PlaceOrderCommandHandler placeOrderCommandHandler;
    @Autowired OrderRepository orders;

    private final AgeRestriction alcohol = new AgeRestriction(Map.of(
        Country.of("PL"), 18,
        Country.of("GB"), 18
    ));

    private PlaceOrderCommand command(int buyerAge) {
        return new PlaceOrderCommand(
            UUID.randomUUID().toString(),
            buyerAge,
            Country.of("PL"),
            List.of(new PlaceOrderCommand.Item(
                UUID.randomUUID().toString(),
                "Wino X", Money.of("50.00", "PLN"), 1, alcohol))
        );
    }

    @Test
    void adultCanPlaceAnOrderWithRestrictedProduct() {
        var id = placeOrderCommandHandler.handle(command(18));
        assertThat(orders.findById(id)).isPresent();
    }

    @Test
    void underageBuyerIsRejectedAndNothingIsPersisted() {
        assertThatThrownBy(() -> placeOrderCommandHandler.handle(command(17)))
            .isInstanceOf(AgeRestrictionViolation.class)
            .hasMessageContaining("Wino X");
    }
}