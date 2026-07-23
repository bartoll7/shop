package com.example.shop.ordering.application;

import com.example.shop.ordering.domain.CustomerId;
import com.example.shop.ordering.domain.Order;
import com.example.shop.ordering.domain.OrderRepository;
import com.example.shop.shared.Money;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

/**
 * CustomerOrdersQueryService — a QUERY SERVICE, owned by the application layer.
 *
 * <p>This service is a query service: it retrieves data from the domain and transforms it
 * into a form suitable for the presentation layer. It does not modify the domain state.
 *
 * <p>It is NOT a domain service: it does not contain domain logic.
 * It is a simple application service that orchestrates retrieval
 * of data from the domain and mapping to DTOs.
 */
@Service
public class CustomerOrdersQueryService {
    private final OrderRepository orderRepository;

    public CustomerOrdersQueryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<CustomerOrderView> ordersOf(CustomerId customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream()
            .map(order -> new CustomerOrderView(
                order.id(),
                order.status(),
                order.lines().size(),
                totalAmount(order),
                getFirstProductName(order)
            ))
            .toList();
    }

    private static @Nullable String getFirstProductName(Order order) {
        return order.lines().isEmpty() ? null : order.lines().getFirst().productName();
    }

    /**
     * Returns the total amount of the order, or null if the order has no lines.
     *
     * <p>We return null for empty orders to avoid returning a Money object with zero value,
     * which could be misleading and with guessing currency. An empty order has no total amount.
     */
    private static @Nullable Money totalAmount(Order order) {
        return order.lines().isEmpty() ? null : order.totalAmount();
    }
}
