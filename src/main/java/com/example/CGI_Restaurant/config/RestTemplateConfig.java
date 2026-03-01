package com.example.CGI_Restaurant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Exposes a RestTemplate bean for outbound HTTP calls (e.g. TheMealDB API).
 */
@Configuration
public class RestTemplateConfig {

    /** Shared RestTemplate for REST client calls. */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
