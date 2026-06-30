package com.example.shop.payment;

import com.example.shop.ordering.application.OrderApplicationService;
import com.example.shop.ordering.application.PlaceOrderCommand;
import com.example.shop.ordering.domain.Order;
import com.example.shop.ordering.domain.OrderId;
import com.example.shop.ordering.domain.OrderRepository;
import com.example.shop.ordering.domain.OrderStatus;
import com.example.shop.payment.application.PaymentApplicationService;
import com.example.shop.shared.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.awaitility.Awaitility;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PaymentFlowIntegrationTest {

    @Autowired OrderApplicationService orders;
    @Autowired PaymentApplicationService payments;
    @Autowired OrderRepository orderRepository;

    @Test
    void payingThroughTheProviderMarksTheOrderAsPaidEndToEnd() {
        // 1. Place an order.
        OrderId orderId = orders.placeOrder(new PlaceOrderCommand(
            UUID.randomUUID().toString(),
            30,
            com.example.shop.shared.Country.of("PL"),
            List.of(new PlaceOrderCommand.Item(
                UUID.randomUUID().toString(),
                "Wino X", Money.of("50.00", "PLN"), 2,
                com.example.shop.agerestriction.domain.AgeRestriction.none()))
        ));

        // 2. Initiate payment — the fake provider will webhook back, the ACL translates,
        //    payment confirms, PaymentReceived fires, ordering marks the order paid.
        payments.initiatePayment(orderId, Money.of("100.00", "PLN"));

        // 3. The whole chain is synchronous here, but assert resiliently anyway.
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
           assertThat(orderRepository.findById(orderId))
               .get()
               .extracting(Order::status)
               .isEqualTo(OrderStatus.PAID));
    }
}