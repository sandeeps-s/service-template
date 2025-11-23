package com.microplat.service_template;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
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

    @Test
    void whenDownstreamReturnsError_thenControllerReturnsProblemDetail502() {
        // Downstream returns 500
        stubFor(get(urlEqualTo("/weather?city=Paris"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"boom\"}")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather").queryParam("city", "Paris").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(502)
                .expectHeader().contentTypeCompatibleWith(MediaType.valueOf("application/problem+json"))
                .expectBody()
                .jsonPath("$.title").isEqualTo("Downstream service error")
                .jsonPath("$.status").isEqualTo(502)
                .jsonPath("$.downstreamStatus").isEqualTo(500);
    }

    @Test
    public void testCircuitBreakerWithRetry() {
        stubFor(get(urlEqualTo("/weather?city=Paris"))
                .willReturn(ok()));
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather").queryParam("city", "Paris").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather").queryParam("city", "Paris").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        verify(2, getRequestedFor(urlEqualTo("/weather?city=Paris")));

        stubFor(get(urlEqualTo("/weather?city=Paris"))
                .willReturn(serverError()));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather").queryParam("city", "Paris").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        verify(5, getRequestedFor(urlEqualTo("/weather?city=Paris")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather").queryParam("city", "Paris").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);

        verify(5, getRequestedFor(urlEqualTo("/weather?city=Paris")));
    }

    @Test
    public void testRetry() {
        stubFor(get(urlEqualTo("/weather?city=Paris"))
                .willReturn(ok()));
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather").queryParam("city", "Paris").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        verify(1, getRequestedFor(urlEqualTo("/weather?city=Paris")));

        resetAllRequests();

        stubFor(get(urlEqualTo("/weather?city=Paris"))
                .willReturn(serverError()));
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather").queryParam("city", "Paris").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        verify(3, getRequestedFor(urlEqualTo("/weather?city=Paris")));
    }

    @Test
    public void testTimeout() {
        stubFor(get(urlEqualTo("/weather?city=Paris"))
                .willReturn(ok().withFixedDelay(5000)));
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather").queryParam("city", "Paris").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.REQUEST_TIMEOUT);
    }

}
