package com.microplat.service_template.domain;

import java.util.Optional;

/**
 * Domain-level repository abstraction for Orders.
 * This interface is infrastructure-agnostic; adapters in infra should implement it.
 */
public interface OrderRepository {

    Optional<Order> findByOrderId(String orderId);

    Order save(Order order);
}
