package com.itachallenge.githubcore.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "github")
public class GithubProperties {
    private String baseUrl;
    private String tokenUri;
    private String clientId;
    private String clientSecret;
    private String userInfoUri;
}
