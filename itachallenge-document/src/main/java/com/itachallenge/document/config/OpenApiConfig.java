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
        String jsonApiSpecScore = documentService.getSwaggerScoreDocsStr();
        String jsonApiSpecAuth = documentService.getSwaggerAuthDocsStr();

        OpenAPI challengeApi = parseOpenApiSpec(jsonApiSpecChallenge);
        OpenAPI userApi = parseOpenApiSpec(jsonApiSpecUser);
        OpenAPI scoreApi = parseOpenApiSpec(jsonApiSpecScore);
        OpenAPI authApi = parseOpenApiSpec(jsonApiSpecAuth);

        OpenAPI allApi = new OpenAPI();

        allApi.setInfo(new Info()
                .title("ITA Challenge: Documentació APIs del projecte")
                .version("1.0")
                .description("**Selecciona la funcionalitat desitjada en el desplegable superior dret**"));

        allApi.setExtensions(Map.of("itachallenge-challenge-api", challengeApi,
                "itachallenge-user-api", userApi, "itachallenge-score-api", scoreApi, "itachallenge-auth-api", authApi));

        return allApi;
    }

    public static OpenAPI parseOpenApiSpec(String spec) throws JsonProcessingException {

        ObjectMapper objectMapper = Yaml.mapper();
        try {
            return objectMapper.readValue(spec, OpenAPI.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing OpenAPI spec: " + e.getMessage());
        }
    }
}

