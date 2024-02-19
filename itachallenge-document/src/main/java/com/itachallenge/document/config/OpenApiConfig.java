package com.itachallenge.document.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.document.service.DocumentService;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenApiConfig {
    private final DocumentService documentService;

    public OpenApiConfig(DocumentService documentService) {
        this.documentService = documentService;
    }

    public static OpenAPI parseOpenApiSpec(String spec) {

        ObjectMapper objectMapper = Json.mapper();
        try {
            return objectMapper.readValue(spec, OpenAPI.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing OpenAPI spec: " + e.getMessage());
        }
    }

    @Bean
    public OpenAPI allOpenAPI() {

        String jsonApiSpecChallenge = documentService.getSwaggerChallengeDocsStr();
        String jsonApiSpecUser = documentService.getSwaggerUserDocsStr();
        String jsonApiSpecScore = documentService.getSwaggerScoreDocsStr();
        String jsonApiSpecAuth = documentService.getSwaggerAuthDocsStr();

        OpenAPI challengeApi = parseOpenApiSpec(jsonApiSpecChallenge);
        OpenAPI userApi = parseOpenApiSpec(jsonApiSpecUser);
        OpenAPI scoreApi = parseOpenApiSpec(jsonApiSpecScore);
        OpenAPI authApi = parseOpenApiSpec(jsonApiSpecAuth);

        OpenAPI allApi = new OpenAPI();

        allApi.setInfo(new Info()
                .title("ITA Challenges APIs Documentation")
                .version("1.0")
                .description("Centralized documentation for ITA Challenges APIs. Explore and understand the available services for authentication, challenges, user management, scoring, and more."));
        allApi.setExtensions(Map.of("itachallenge-challenge-api", challengeApi,
                "itachallenge-user-api", userApi, "itachallenge-score-api", scoreApi, "itachallenge-auth-api", authApi));

        ExternalDocumentation WIKIExternalDocs = new ExternalDocumentation()
                .description("IT Academy Wiki")
                .url("https://dev.api.itawiki.eurecatacademy.org/api/v1/api-docs");
        allApi.setExternalDocs(WIKIExternalDocs);

        return allApi;
    }
}

