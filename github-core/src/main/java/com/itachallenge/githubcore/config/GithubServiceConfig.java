package com.itachallenge.githubcore.config;

import com.itachallenge.githubcore.service.GithubApiServiceImpl;
import com.itachallenge.githubcore.service.GithubApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GithubServiceConfig {

    @Value("${github.user-info-uri}")
    private String githubApiUrl;

    @Value("${github.token-uri}")
    private String githubTokenUrl;

    @Bean
    public GithubApiService githubApiService(WebClient.Builder webClientBuilder) {
        return new GithubApiServiceImpl(webClientBuilder, githubApiUrl, githubTokenUrl);
    }
}