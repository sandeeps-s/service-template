package com.microplat.service_template.app;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

@Component
public class ExternalApiCaller {

    private final WebClient externalApiWebClient;

    public ExternalApiCaller(@Qualifier("externalApiWebClient") WebClient externalApiWebClient) {
        this.externalApiWebClient = externalApiWebClient;
    }

    @Retry(name = "WeatherRetry")
    @CircuitBreaker(name= "WeatherCB")
    @TimeLimiter(name="WeatherTL")
    public CompletableFuture<WeatherData> getWeatherData(String city) {
        return externalApiWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/weather").queryParam("city", city).build())
                .retrieve()
                .bodyToMono(WeatherData.class)
                .toFuture();
    }
}
