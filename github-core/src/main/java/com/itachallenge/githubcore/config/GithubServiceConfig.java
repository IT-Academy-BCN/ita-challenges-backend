package com.itachallenge.githubcore.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.githubcore.service.GithubApiServiceImpl;
import com.itachallenge.githubcore.service.GithubApiService;
import com.itachallenge.githubcore.service.GithubOAuthService;
import com.itachallenge.githubcore.service.GithubOAuthServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GithubServiceConfig {

    @Value("${github.user-info-uri}")
    private String githubApiUrl;

    @Value("${spring.security.oauth2.client.provider.github.token-uri}")
    private String githubTokenUri;

    @Value("${spring.security.oauth2.client.provider.github.user-info-uri}")
    private String githubUserInfoUri;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    @Bean
    public GithubApiService githubApiService(WebClient.Builder webClientBuilder) {
        return new GithubApiServiceImpl(webClientBuilder, githubApiUrl);
    }

    @Bean
    public GithubOAuthService githubOAuthService(WebClient.Builder webClientBuilder,
                                                 ObjectMapper objectMapper) {
        return new GithubOAuthServiceImpl(
                webClientBuilder,
                objectMapper,
                githubTokenUri,
                githubUserInfoUri,
                clientId,
                clientSecret
        );
    }
}
