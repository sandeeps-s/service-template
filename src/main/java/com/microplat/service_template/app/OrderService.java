package com.microplat.service_template.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microplat.service_template.domain.Order;
import com.microplat.service_template.domain.OrderCreated;
import com.microplat.service_template.domain.OrderRepository;
import com.microplat.service_template.infra.db.entity.OutboxEventRecord;
import com.microplat.service_template.infra.db.repo.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository,
                        OutboxEventRepository outboxRepository,
                        ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Order createOrder(String orderId, BigDecimal amount) {
        Order saved = orderRepository.save(Order.now(orderId, amount));

        OrderCreated event = new OrderCreated(
                UUID.randomUUID().toString(),
                Instant.now(),
                saved.orderId(),
                saved.amount()
        );
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            // If serialization fails, propagate to rollback the transaction
            throw new RuntimeException("Failed to serialize OrderCreated event", e);
        }
        outboxRepository.save(new OutboxEventRecord(event.eventId(), event.type(), payload));
        return saved;
    }
}
