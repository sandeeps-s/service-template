package com.microplat.service_template.domain;

public interface CheckoutEventHandler {
    void handleCheckoutCompleted(CheckoutCompleted checkoutCompleted);
}
