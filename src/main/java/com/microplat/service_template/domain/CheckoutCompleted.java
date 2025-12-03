package com.microplat.service_template.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record CheckoutCompleted(String eventId, Instant occurredAt, String orderId,
                                BigDecimal amount, String paymentId) implements DomainEvent{
    @Override
    public String type() {
        return "checkout.completed.v1";
    }
}
