package com.microplat.service_template.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Domain model for an Order. This type is infrastructure-agnostic.
 */
public record Order(String orderId, BigDecimal amount, Instant createdAt) {

    /**
     * Convenience factory to create an Order with the current timestamp.
     */
    public static Order now(String orderId, BigDecimal amount) {
        return new Order(orderId, amount, Instant.now());
    }
}
