package com.itachallenge.githubcore.config;

import com.itachallenge.githubcore.service.GithubApiServiceImpl;
import com.itachallenge.githubcore.service.GithubApiService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(GithubProperties.class)
public class GithubServiceConfig {

    @Bean
    public GithubApiService githubApiService(GithubProperties githubProperties, WebClient.Builder webClientBuilder) {
        return new GithubApiServiceImpl(githubProperties, webClientBuilder);
    }
}
