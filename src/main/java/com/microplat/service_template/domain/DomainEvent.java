package com.microplat.service_template.domain;

import java.time.Instant;

public interface DomainEvent {
    String eventId();
    Instant occurredAt();
    String type();
}
