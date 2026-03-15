package com.sentinel.agent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class RestTemplateConfig {

    @Value("${sentinel.api.key}")
    private String apiKey;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Create an interceptor that adds the header before the request leaves the Agent
        ClientHttpRequestInterceptor headerInterceptor = (request, body, execution) -> {
            request.getHeaders().add("X-API-KEY", apiKey);
            return execution.execute(request, body);
        };

        // Attach the interceptor to the RestTemplate
        restTemplate.setInterceptors(Collections.singletonList(headerInterceptor));

        return restTemplate;
    }
}