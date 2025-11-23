package com.microplat.service_template.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ExternalApiConfig {

    @Bean(name = "externalApiWebClient")
    public WebClient externalApiWebClient(WebClient.Builder builder,
                                          @Value("${external.api.base-url}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}
