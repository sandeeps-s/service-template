package com.microplat.service_template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServiceTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceTemplateApplication.class, args);
    }

}
