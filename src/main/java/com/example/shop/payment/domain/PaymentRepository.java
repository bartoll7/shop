package com.example.shop.payment.domain;

import com.example.shop.ordering.domain.OrderId;

import java.util.Optional;

public interface PaymentRepository {
    void save(Payment payment);
    Optional<Payment> findById(PaymentId id);
    Optional<Payment> findByOrderId(OrderId orderId);
}