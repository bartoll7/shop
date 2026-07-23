package com.example.shop.returns.domain;

import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.Money;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ReturnAggregateTest {

    private static final OrderId ORDER = OrderId.newId();
    private static final Money REFUND = Money.of("120.00", "PLN");

    @Test
    void requestStartsInRequestedAndEmitsReturnRequestedCarryingOrderAndAmount() {
        Return aReturn = Return.request(ORDER, REFUND);

        assertThat(aReturn.status()).isEqualTo(ReturnStatus.REQUESTED);
        assertThat(aReturn.orderId()).isEqualTo(ORDER);
        assertThat(aReturn.refundAmount()).isEqualTo(REFUND);

        List<DomainEvent> events = aReturn.pullDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.getFirst())
            .isInstanceOfSatisfying(ReturnRequested.class, e -> {
                assertThat(e.orderId()).isEqualTo(ORDER);
                assertThat(e.refundAmount()).isEqualTo(REFUND);
            });
    }

    @Test
    void markAsSentChangesStatusToSentAndEmitsReturnSent() {
        Return aReturn = Return.request(ORDER, REFUND);
        aReturn.markAsSent();

        assertThat(aReturn.status()).isEqualTo(ReturnStatus.SENT);

        List<DomainEvent> events = aReturn.pullDomainEvents();
        assertThat(events).hasSize(2);
        assertThat(events.getLast())
            .isInstanceOfSatisfying(ReturnSent.class, e -> {
                assertThat(e.orderId()).isEqualTo(ORDER);
                assertThat(e.refundAmount()).isEqualTo(REFUND);
            });
    }

    @Test
    void fullCycleRequestMarkSentMarkDeliveredAccept() {
        Return aReturn = Return.request(ORDER, REFUND);
        aReturn.markAsSent();
        aReturn.markAsDelivered();
        aReturn.accept();

        assertThat(aReturn.status()).isEqualTo(ReturnStatus.ACCEPTED);

        List<DomainEvent> events = aReturn.pullDomainEvents();
        assertThat(events).hasSize(3);
        assertThat(events.getLast())
            .isInstanceOfSatisfying(ReturnAccepted.class, e -> {
                assertThat(e.orderId()).isEqualTo(ORDER);
                assertThat(e.refundAmount()).isEqualTo(REFUND);
            });
    }

    @Test
    void acceptBeforeDelivered() {
        Return aReturn = Return.request(ORDER, REFUND);
        assertThatThrownBy(aReturn::accept)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Only a DELIVERED return can be accepted (was: REQUESTED)");
    }

    @Test
    void markDeliveredBeforeSent() {
        Return aReturn = Return.request(ORDER, REFUND);
        assertThatThrownBy(aReturn::markAsDelivered)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Only a SENT return can be delivered (was: REQUESTED)");
    }

    @Test
    void transitionFromRejectedToAccepted() {
        Return aReturn = Return.request(ORDER, REFUND);
        aReturn.markAsSent();
        aReturn.markAsDelivered();
        aReturn.reject();

        assertThatThrownBy(aReturn::accept)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Only a DELIVERED return can be accepted (was: REJECTED)");
    }

    @Test
    void transitionFromTerminalStateToAnotherTerminalState() {
        Return aReturn = Return.request(ORDER, REFUND);
        aReturn.markAsSent();
        aReturn.markAsDelivered();
        aReturn.accept();

        assertThatThrownBy(aReturn::reject)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Only a DELIVERED return can be rejected (was: ACCEPTED)");
    }

    @Test
    void requestWithNegativeAmount() {
        assertThatThrownBy(() -> Return.request(ORDER, Money.of("-10.00", "PLN")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Refund amount must not be negative or zero");
    }

    @Test
    void requestWithZeroAmount() {
        assertThatThrownBy(() -> Return.request(ORDER, Money.of("0.00", "PLN")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Refund amount must not be negative or zero");
    }
}
