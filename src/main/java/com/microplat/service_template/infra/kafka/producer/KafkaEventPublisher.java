package com.microplat.service_template.infra.kafka.producer;

import com.microplat.service_template.domain.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public abstract class KafkaEventPublisher<T extends DomainEvent> {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);
    private final KafkaTemplate<String, T> kafkaTemplate;
    private final String topic;

    protected KafkaEventPublisher(KafkaTemplate<String, T> kafkaTemplate, String topic){
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    protected void send(String key, T event){
        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) ->{
            if(ex != null){
                log.error(ex.getMessage(), ex);
            }else{
                log.info("Event published {}", result);
            }
        });
    }
}
