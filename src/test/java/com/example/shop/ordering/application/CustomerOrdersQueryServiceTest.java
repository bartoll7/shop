package com.example.shop.ordering.application;

import com.example.shop.ordering.domain.CustomerId;
import com.example.shop.ordering.domain.Order;
import com.example.shop.ordering.domain.OrderStatus;
import com.example.shop.ordering.domain.ProductId;
import com.example.shop.ordering.domain.Quantity;
import com.example.shop.ordering.infrastructure.InMemoryOrderRepository;
import com.example.shop.shared.Money;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Drives the "My orders" read use case built DELIBERATELY through the write model
 * (aggregate + repository port) — the single-model way, so the cost of it is felt
 * before CQRS separates the read model out (lessons 2.3/2.4).
 */
class CustomerOrdersQueryServiceTest {

    private final InMemoryOrderRepository repository = new InMemoryOrderRepository();
    private final CustomerOrdersQueryService service = new CustomerOrdersQueryService(repository);

    @Test
    void mapsEachCustomerOrderToAViewWithLineCountTotalAndFirstProductName() {
        CustomerId customer = CustomerId.of(UUID.randomUUID().toString());

        Order first = Order.place(customer);
        first.addLine(ProductId.of(UUID.randomUUID().toString()), "Wino X", Money.of("50.00", "PLN"), Quantity.of(2));
        first.addLine(ProductId.of(UUID.randomUUID().toString()), "Ser Y", Money.of("20.00", "PLN"), Quantity.of(1));
        repository.save(first);

        Order second = Order.place(customer);
        second.addLine(ProductId.of(UUID.randomUUID().toString()), "Chleb Z", Money.of("8.00", "PLN"), Quantity.of(3));
        repository.save(second);

        List<CustomerOrderView> views = service.ordersOf(customer);

        assertThat(views).hasSize(2);
        assertThat(views).anySatisfy(view -> {
            assertThat(view.orderId()).isEqualTo(first.id());
            assertThat(view.status()).isEqualTo(OrderStatus.PLACED);
            assertThat(view.lineCount()).isEqualTo(2);
            assertThat(view.total()).isEqualTo(Money.of("120.00", "PLN"));
            assertThat(view.firstProductName()).isEqualTo("Wino X");
        });
        assertThat(views).anySatisfy(view -> {
            assertThat(view.orderId()).isEqualTo(second.id());
            assertThat(view.lineCount()).isEqualTo(1);
            assertThat(view.total()).isEqualTo(Money.of("24.00", "PLN"));
            assertThat(view.firstProductName()).isEqualTo("Chleb Z");
        });
    }

    @Test
    void returnsOnlyOrdersBelongingToTheGivenCustomer() {
        CustomerId customer = CustomerId.of(UUID.randomUUID().toString());
        CustomerId otherCustomer = CustomerId.of(UUID.randomUUID().toString());

        Order mine = Order.place(customer);
        mine.addLine(ProductId.of(UUID.randomUUID().toString()), "Wino X", Money.of("50.00", "PLN"), Quantity.of(1));
        repository.save(mine);

        Order theirs = Order.place(otherCustomer);
        theirs.addLine(ProductId.of(UUID.randomUUID().toString()), "Ser Y", Money.of("20.00", "PLN"), Quantity.of(1));
        repository.save(theirs);

        List<CustomerOrderView> views = service.ordersOf(customer);

        assertThat(views).hasSize(1);
        assertThat(views.getFirst().orderId()).isEqualTo(mine.id());
    }

    @Test
    void returnsEmptyListForCustomerWithoutAnyOrders() {
        CustomerId customerWithNoOrders = CustomerId.of(UUID.randomUUID().toString());

        List<CustomerOrderView> views = service.ordersOf(customerWithNoOrders);

        assertThat(views).isEmpty();
    }

    /**
     * This test demonstrates that the query service can handle orders with no lines,
     * even though the domain model does not allow such orders to be paid or shipped.
     * The query service should still return a view with lineCount = 0 and total = null.
     */
    @Test
    void handlesOrderWithNoLines() {
        CustomerId customer = CustomerId.of(UUID.randomUUID().toString());

        Order orderWithNoLines = Order.place(customer);
        repository.save(orderWithNoLines);

        List<CustomerOrderView> views = service.ordersOf(customer);

        assertThat(views).hasSize(1);
        CustomerOrderView view = views.getFirst();
        assertThat(view.orderId()).isEqualTo(orderWithNoLines.id());
        assertThat(view.status()).isEqualTo(OrderStatus.PLACED);
        assertThat(view.lineCount()).isEqualTo(0);
        assertThat(view.total()).isNull();
        assertThat(view.firstProductName()).isNull();
    }
}
