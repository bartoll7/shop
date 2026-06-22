package com.example.shop.payment.infrastructure;

import com.example.shop.ordering.domain.OrderId;
import com.example.shop.payment.domain.Payment;
import com.example.shop.payment.domain.PaymentId;
import com.example.shop.payment.domain.PaymentRepository;
import com.example.shop.payment.domain.PaymentStatus;
import com.example.shop.shared.Money;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPaymentRepository implements PaymentRepository {

    private record Stored(PaymentId id, OrderId orderId, Money amount, PaymentStatus status) {
    }

    private final Map<PaymentId, Stored> store = new ConcurrentHashMap<>();

    @Override
    public void save(Payment payment) {
        store.put(payment.id(), new Stored(
            payment.id(), payment.orderId(), payment.amount(), payment.status()));
    }

    @Override
    public Optional<Payment> findById(PaymentId id) {
        return Optional.ofNullable(store.get(id)).map(this::rebuild);
    }

    @Override
    public Optional<Payment> findByOrderId(OrderId orderId) {
        return store.values().stream()
            .filter(s -> s.orderId().equals(orderId))
            .findFirst()
            .map(this::rebuild);
    }

    private Payment rebuild(Stored s) {
        return Payment.reconstitute(s.id(), s.orderId(), s.amount(), s.status());
    }
}