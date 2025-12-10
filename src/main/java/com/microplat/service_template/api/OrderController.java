package com.microplat.service_template.api;

import com.microplat.service_template.app.OrderService;
import com.microplat.service_template.domain.Order;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping(path = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
@Profile({"test", "postgres"})
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Order> create(@RequestBody OrderRequest request) {
        return Mono.fromSupplier(() -> orderService.createOrder(request.orderId(), request.amount()));
    }

    public record OrderRequest(String orderId, BigDecimal amount) {}
}
