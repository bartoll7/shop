package com.example.shop.loyalty;

import com.example.shop.agerestriction.domain.AgeRestriction;
import com.example.shop.loyalty.domain.CustomerId;
import com.example.shop.loyalty.domain.LoyaltyAccount;
import com.example.shop.loyalty.domain.LoyaltyAccountRepository;
import com.example.shop.ordering.application.PayOrderCommand;
import com.example.shop.ordering.application.PayOrderCommandHandler;
import com.example.shop.ordering.application.PlaceOrderCommand;
import com.example.shop.ordering.application.PlaceOrderCommandHandler;
import com.example.shop.ordering.domain.OrderId;
import com.example.shop.shared.Country;
import com.example.shop.shared.Money;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Kata 1.2 — Eventual consistency between aggregates.
 *
 * <p>Paying an order must credit loyalty points to the customer. Loyalty is a SEPARATE
 * aggregate in a SEPARATE context, reacting to OrderPaid — NOT modified inside the same
 * transaction as the order. So a failure to award points must never roll back a real
 * payment, and Order must know nothing about loyalty.
 *
 * <p>These tests are the acceptance criteria. Make them compile and pass by writing the
 * production code yourself — do NOT change the tests.
 */
@SpringBootTest
class LoyaltyPointsIntegrationTest {

    @Autowired PlaceOrderCommandHandler placeOrderCommandHandler;
    @Autowired PayOrderCommandHandler payOrderCommandHandler;
    @Autowired LoyaltyAccountRepository loyaltyAccounts;

    @Test
    void payingAnOrderAwardsLoyaltyPointsToTheCustomerAfterCommit() {
        CustomerId customer = CustomerId.newId();

        // Order total = 2 x 50.00 PLN = 100.00 PLN
        OrderId orderId = placeOrderCommandHandler.handle(new PlaceOrderCommand(
            customer.value().toString(),
            30,
            Country.of("PL"),
            List.of(new PlaceOrderCommand.Item(
                UUID.randomUUID().toString(),
                "Wino X", Money.of("50.00", "PLN"), 2,
                AgeRestriction.none()))
        ));

        // Before payment there are no points for this customer.
        assertThat(loyaltyAccounts.findByCustomer(customer)).isEmpty();

        payOrderCommandHandler.handle(new PayOrderCommand(orderId.toString()));

        // After the order-payment transaction commits, the loyalty context reacts
        // (eventual consistency): 1 point per 1 PLN of the order total -> 100 points.
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
            assertThat(loyaltyAccounts.findByCustomer(customer))
                .get()
                .extracting(LoyaltyAccount::points)
                .isEqualTo(100));
    }
}
