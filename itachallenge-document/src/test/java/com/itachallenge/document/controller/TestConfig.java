package com.itachallenge.document.controller;

import com.itachallenge.document.config.OpenApiConfig;
import com.itachallenge.document.service.DocumentService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    private DocumentService documentService;

    @Bean
    public OpenApiConfig openApiConfig() {
        return new OpenApiConfig(documentService);
    }
}
