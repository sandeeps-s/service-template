package com.microplat.service_template.infra.db.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "outbox_events")
public class OutboxEventRecord {

    @Id
    @Column(name = "event_id", length = 100, nullable = false)
    private String eventId;

    @Column(name = "type", length = 200, nullable = false)
    private String type;

    @Lob
    @Column(name = "payload", nullable = false)
    private String payload;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "processed_at")
    private Instant processedAt;

    protected OutboxEventRecord() {
    }

    public OutboxEventRecord(String eventId, String type, String payload) {
        this.eventId = eventId;
        this.type = type;
        this.payload = payload;
    }

    public String getEventId() { return eventId; }
    public String getType() { return type; }
    public String getPayload() { return payload; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getProcessedAt() { return processedAt; }

    public void markProcessed(Instant when) {
        this.processedAt = when;
    }
}
