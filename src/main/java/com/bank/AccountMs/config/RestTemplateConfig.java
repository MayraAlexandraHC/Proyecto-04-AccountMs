package com.bank.AccountMs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RestTemplateConfig {

    @Value("${customerms.url}")
    private String customerMsUrl;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(customerMsUrl)
                .build();
    }
}