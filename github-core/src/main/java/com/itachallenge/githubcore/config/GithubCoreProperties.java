package com.itachallenge.githubcore.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "github-core")

public class GithubCoreProperties {

    @Getter
    @Setter
    private String userInfoUri;

    @Getter
    @Setter
    private String tokenUri;

    @Getter
    @Setter
    private String authorizationUri;

    @Getter
    @Setter
    private String clientId;

    @Getter
    @Setter
    private String clientSecret;

}