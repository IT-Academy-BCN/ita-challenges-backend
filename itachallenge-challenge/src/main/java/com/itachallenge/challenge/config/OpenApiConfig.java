package com.itachallenge.challenge.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Ita Backend Challenges", version = "1.0", description = "Documentation API"))
public class OpenApiConfig {

}
