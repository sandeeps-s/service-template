package com.microplat.service_template;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class WeatherControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void givenExternalApiStub_whenCallingControllerEndpoint_thenWeGetWeatherData() {
        // Stub downstream external API
        stubFor(get(urlEqualTo("/weather?city=London"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"city\": \"London\", \"temperature\": 20, \"description\": \"Cloudy\"}")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather").queryParam("city", "London").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.city").isEqualTo("London")
                .jsonPath("$.temperature").isEqualTo(20)
                .jsonPath("$.description").isEqualTo("Cloudy");
    }
}
