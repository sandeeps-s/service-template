package com.microplat.service_template.infra.kafka.producer;

import com.microplat.service_template.domain.OrderCreated;
import com.microplat.service_template.domain.OrderEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderEventPublisher extends KafkaEventPublisher<OrderCreated> implements OrderEventPublisher {

    public KafkaOrderEventPublisher(
            KafkaTemplate<String, OrderCreated> kafkaTemplate,
            @Value("${topics.orderCreated}") String topic
    ) {
        super(kafkaTemplate, topic);
    }

    @Override
    public void publishOrderCreated(OrderCreated orderCreated) {
        send(orderCreated.orderId(), orderCreated);
    }
}
