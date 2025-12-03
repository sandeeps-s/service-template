package com.microplat.service_template.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderCreated(String eventId, Instant occurredAt, String orderId,
                           BigDecimal amount) implements DomainEvent {

    @Override
    public String type() {
        return "order.created.v1";
    }
}
