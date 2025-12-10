package com.microplat.service_template;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrderControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void createOrder_shouldReturn201AndPayload() {
        String body = "{" +
                "\"orderId\":\"order-123\"," +
                "\"amount\": 99.95" +
                "}";

        webTestClient.post()
                .uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.orderId").isEqualTo("order-123")
                .jsonPath("$.amount").isEqualTo(99.95)
                .jsonPath("$.createdAt").isNotEmpty();
    }
}
