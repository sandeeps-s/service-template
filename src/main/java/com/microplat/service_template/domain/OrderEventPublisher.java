package com.microplat.service_template.domain;

public interface OrderEventPublisher {
    void publishOrderCreated(OrderCreated orderCreated);
}
