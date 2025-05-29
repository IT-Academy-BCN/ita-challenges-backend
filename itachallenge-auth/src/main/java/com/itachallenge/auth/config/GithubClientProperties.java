package com.itachallenge.auth.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.github")
public class GithubClientProperties {
    private Map<String, ClientConfig> environments;

    public ClientConfig getClientConfig(String env) {
        return environments.get(env);
    }

    @Data
    public static class ClientConfig {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
    }
}