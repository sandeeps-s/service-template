package com.microplat.service_template.infra.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microplat.service_template.domain.OrderCreated;
import com.microplat.service_template.domain.OrderEventPublisher;
import com.microplat.service_template.infra.db.entity.OutboxEventRecord;
import com.microplat.service_template.infra.db.repo.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@Profile({"test", "postgres"})
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxEventRepository outboxRepo;
    private final OrderEventPublisher orderEventPublisher;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(OutboxEventRepository outboxRepo,
                           OrderEventPublisher orderEventPublisher,
                           ObjectMapper objectMapper) {
        this.outboxRepo = outboxRepo;
        this.orderEventPublisher = orderEventPublisher;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishPending() {
        List<OutboxEventRecord> batch = outboxRepo.findByProcessedAtIsNullOrderByCreatedAtAsc(PageRequest.of(0, 50));
        for (OutboxEventRecord rec : batch) {
            try {
                switch (rec.getType()) {
                    case "order.created.v1" -> {
                        OrderCreated evt = objectMapper.readValue(rec.getPayload(), OrderCreated.class);
                        orderEventPublisher.publishOrderCreated(evt);
                    }
                    default -> {
                        log.warn("Skipping unknown outbox event type: {}", rec.getType());
                        // mark processed to avoid stuck events of unknown types
                    }
                }
                rec.markProcessed(Instant.now());
                outboxRepo.save(rec);
            } catch (Exception e) {
                // Leave as unprocessed to retry later
                log.error("Failed to publish outbox event {} of type {}", rec.getEventId(), rec.getType(), e);
            }
        }
    }
}
