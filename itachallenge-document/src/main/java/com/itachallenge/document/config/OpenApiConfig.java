package com.itachallenge.document.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Ita Backend Challenges", version = "1.1.0-RELEASE", description = "Documentation API"))
public class OpenApiConfig {

    @Bean
    public List<GroupedOpenApi> groupedOpenApis() {
        return Arrays.asList(
                GroupedOpenApi.builder().group("users").pathsToMatch("/api/users/**").build(),
                GroupedOpenApi.builder().group("orders").pathsToMatch("/api/orders/**").build()
                // Agrega más GroupedOpenApi para otros microservicios
        );
    }
}

