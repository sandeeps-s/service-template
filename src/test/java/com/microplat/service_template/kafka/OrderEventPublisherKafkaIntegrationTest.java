package com.microplat.service_template.kafka;

import com.microplat.service_template.domain.OrderCreated;
import com.microplat.service_template.domain.OrderEventPublisher;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {OrderEventPublisherKafkaIntegrationTest.ORDER_CREATED_TOPIC})
@ActiveProfiles("test")
@TestPropertySource(properties = {
        // Point Spring Kafka to the embedded broker
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        // Use a dedicated test topic
        "topics.orderCreated=" + OrderEventPublisherKafkaIntegrationTest.ORDER_CREATED_TOPIC,
        // Satisfy ExternalApiConfig bean without starting WireMock
        "external.api.base-url=http://localhost:0"
})
public class OrderEventPublisherKafkaIntegrationTest {

    static final String ORDER_CREATED_TOPIC = "order.created.test";

    @Autowired
    private OrderEventPublisher orderEventPublisher;

    private Consumer<String, OrderCreated> consumer;

    @Value("${spring.embedded.kafka.brokers}")
    private String embeddedBrokers;

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void whenPublishOrderCreated_thenMessageIsWrittenToKafkaTopic() {
        // Arrange: create a Consumer configured to read OrderCreated from the test topic
        java.util.HashMap<String, Object> consumerProps = new java.util.HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedBrokers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "order-created-test-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        JsonDeserializer<OrderCreated> valueDeserializer = new JsonDeserializer<>(OrderCreated.class, false);
        valueDeserializer.addTrustedPackages("*");

        DefaultKafkaConsumerFactory<String, OrderCreated> cf = new DefaultKafkaConsumerFactory<>(
                consumerProps,
                new StringDeserializer(),
                valueDeserializer
        );

        consumer = cf.createConsumer();
        consumer.subscribe(List.of(ORDER_CREATED_TOPIC));

        // Act: publish an OrderCreated domain event
        String orderId = "ORD-123";
        OrderCreated event = new OrderCreated(
                "evt-1",
                Instant.now(),
                orderId,
                new BigDecimal("19.99")
        );

        orderEventPublisher.publishOrderCreated(event);

        // Assert: we can read the record and it matches the published event
        ConsumerRecord<String, OrderCreated> record = KafkaTestUtils.getSingleRecord(consumer, ORDER_CREATED_TOPIC, Duration.ofSeconds(10));
        assertNotNull(record, "Expected a record to be published to the topic");
        assertEquals(orderId, record.key());
        OrderCreated received = record.value();
        assertNotNull(received);
        assertEquals(event.orderId(), received.orderId());
        assertEquals(event.amount(), received.amount());
        assertEquals(event.type(), received.type());
    }
}
