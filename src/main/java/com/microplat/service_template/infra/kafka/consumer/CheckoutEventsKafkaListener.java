package com.microplat.service_template.infra.kafka.consumer;

import com.microplat.service_template.domain.CheckoutCompleted;
import com.microplat.service_template.domain.CheckoutEventHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.stereotype.Component;

@Component
public class CheckoutEventsKafkaListener {

    private final CheckoutEventHandler checkoutEventHandler;

    public CheckoutEventsKafkaListener(CheckoutEventHandler checkoutEventHandler) {
        this.checkoutEventHandler = checkoutEventHandler;
    }

    @KafkaListener(
            topics = "${topics.checkoutCompleted}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onCheckoutCompleted(CheckoutCompleted event){
        checkoutEventHandler.handleCheckoutCompleted(event);
    }
}
