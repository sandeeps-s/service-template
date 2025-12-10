package com.microplat.service_template.infra.kafka.consumer;

import com.microplat.service_template.domain.CheckoutCompleted;
import com.microplat.service_template.domain.CheckoutEventHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(CheckoutEventHandler.class)
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
