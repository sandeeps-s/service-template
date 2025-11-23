package com.microplat.service_template;

import com.microplat.service_template.app.ExternalApiCaller;
import com.microplat.service_template.app.WeatherData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class WeatherServiceIntegrationTest {

    @Autowired
    private ExternalApiCaller externalApiCaller;

    @Value("${wiremock.server.port}")
    private int wireMockPort;

    @Test
    public void  givenExternalApiCallerConfiguredToWireMock_whenGetRequestForACity_thenCallerReceivesSuccessResponse() {
        // Stubbing response for a successful weather data retrieval
        stubFor(get(urlEqualTo("/weather?city=London"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"city\": \"London\", \"temperature\": 20, \"description\": \"Cloudy\"}")));

        // Fetch weather data for London via ExternalApiCaller
        WeatherData weatherData = externalApiCaller.getWeatherData("London");

        assertNotNull(weatherData);
        assertEquals("London", weatherData.city());
        assertEquals(20, weatherData.temperature());
        assertEquals("Cloudy", weatherData.description());
    }
}