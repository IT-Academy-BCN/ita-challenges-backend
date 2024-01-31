package com.itachallenge.auth.config;


import org.springframework.web.reactive.function.client.WebClient;

public class WebClientConfig {
    //@Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
