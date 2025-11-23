package com.microplat.service_template.app;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping(path = "/api/weather", produces = MediaType.APPLICATION_JSON_VALUE)
public class WeatherController {

    private final ExternalApiCaller externalApiCaller;

    public WeatherController(ExternalApiCaller externalApiCaller) {
        this.externalApiCaller = externalApiCaller;
    }

    @GetMapping
    public Mono<WeatherData> getWeather(@RequestParam("city") String city) {
        // ExternalApiCaller currently performs a blocking call via WebClient.block().
        // To keep the controller reactive without refactoring the caller, offload to boundedElastic.
        return Mono.fromCallable(() -> externalApiCaller.getWeatherData(city))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
