package com.itachallenge.githubcore.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.githubcore.service.GithubApiServiceImpl;
import com.itachallenge.githubcore.service.GithubApiService;
import com.itachallenge.githubcore.service.GithubOAuthService;
import com.itachallenge.githubcore.service.GithubOAuthServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(GithubCoreProperties.class)
public class GithubServiceConfig {

    private final GithubCoreProperties properties;

    public GithubServiceConfig(GithubCoreProperties properties) {
        this.properties = properties;
    }

    @Bean
    public GithubApiService githubApiService(WebClient.Builder webClientBuilder) {
        return new GithubApiServiceImpl(webClientBuilder, properties.getUserInfoUri());
    }

    @Bean
    public GithubOAuthService githubOAuthService(WebClient.Builder webClientBuilder,
                                                 ObjectMapper objectMapper) {
        return new GithubOAuthServiceImpl(
                webClientBuilder,
                objectMapper,
                this.properties
        );
    }
}