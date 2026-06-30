package com.example.shop.ordering.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.shop.shared.DomainEvent;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Kata 1.1 — Aggregate boundary: an Order references its Customer BY IDENTITY.
 *
 * <p>The Order aggregate must NOT contain a Customer object. It knows only a CustomerId.
 * This is the "reference by identity" rule: Customer is a separate aggregate with its own
 * lifecycle, so the ordering context holds only the id that points at it.
 *
 * <p>These tests are the acceptance criteria. Make them compile and pass by writing the
 * production code yourself — do NOT change the tests.
 */
class CustomerReferenceTest {

    @Test
    void placed_order_remembers_which_customer_it_belongs_to() {
        CustomerId customer = CustomerId.newId();

        Order order = Order.place(customer);

        assertThat(order.customerId()).isEqualTo(customer);
    }

    @Test
    void an_order_cannot_be_placed_without_a_customer() {
        assertThatThrownBy(() -> Order.place(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void the_OrderPlaced_event_carries_the_customer_id_so_other_contexts_can_react() {
        CustomerId customer = CustomerId.newId();

        Order order = Order.place(customer);

        List<DomainEvent> events = order.pullDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.getFirst()).isInstanceOf(OrderPlaced.class);
        OrderPlaced placed = (OrderPlaced) events.getFirst();
        assertThat(placed.customerId()).isEqualTo(customer);
        assertThat(placed.orderId()).isEqualTo(order.id());
    }

    @Test
    void two_customer_ids_are_equal_only_when_their_value_is_equal() {
        CustomerId a = CustomerId.newId();

        assertThat(a).isEqualTo(new CustomerId(a.value()));
        assertThat(a).isNotEqualTo(CustomerId.newId());
    }
}
