package com.itachallenge.document.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.document.service.DocumentService;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Configuration
//@OpenAPIDefinition(info = @Info(title = "Ita Backend Challenges", version = "1.1.0-RELEASE", description = "Documentation API"))
public class OpenApiConfig {
    static {
        SpringDocUtils.getConfig().addAnnotationsToIgnore(RestController.class);
    }
    private final DocumentService documentService;
    public OpenApiConfig(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Bean
    public OpenAPI allOpenAPI() throws JsonProcessingException {

        String jsonApiSpecChallenge = documentService.getSwaggerChallengeDocsStr();
        String jsonApiSpecUser = documentService.getSwaggerUserDocsStr();
        OpenAPI challengeApi = parseOpenApiSpec(jsonApiSpecChallenge);
        OpenAPI userApi = parseOpenApiSpec(jsonApiSpecUser);

        OpenAPI allApi = new OpenAPI();

        allApi.setInfo(new Info()
                .title("itachallenge-document API Documentation")
                .version("1.0")
                .description("API documentation for itachallenge-document."));

        allApi.setExtensions(Map.of("itachallenge-challenge-api", challengeApi,
                "itachallenge-user-api", userApi));

        return allApi;
    }

    public static OpenAPI parseOpenApiSpec(String spec) throws JsonProcessingException {

        ObjectMapper objectMapper = Yaml.mapper();
        try {
            return objectMapper.readValue(spec, OpenAPI.class);
        } catch (Exception e) {
            // Handle the exception (e.g., log it or throw a custom exception)
            throw new RuntimeException("Error parsing OpenAPI spec: " + e.getMessage(), e);
        }
    }
}

