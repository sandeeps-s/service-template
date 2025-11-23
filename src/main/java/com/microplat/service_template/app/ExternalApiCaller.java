package com.microplat.service_template.app;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ExternalApiCaller {

    private final WebClient externalApiWebClient;

    public ExternalApiCaller(@Qualifier("externalApiWebClient") WebClient externalApiWebClient) {
        this.externalApiWebClient = externalApiWebClient;
    }

    public WeatherData getWeatherData(String city) {
        return externalApiWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/weather").queryParam("city", city).build())
                .retrieve()
                .bodyToMono(WeatherData.class)
                .block();
    }
}
