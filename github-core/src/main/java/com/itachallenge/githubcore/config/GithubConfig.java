package com.itachallenge.githubcore.config;

import com.itachallenge.githubcore.service.GithubApiService;
import com.itachallenge.githubcore.service.IGithubApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GithubConfig {

    @Value("${github.user-info-uri}")
    private String githubApiUrl;

    @Bean
    public IGithubApiService githubApiService(WebClient.Builder webClientBuilder) {
        return new GithubApiService(webClientBuilder, githubApiUrl);
    }
}