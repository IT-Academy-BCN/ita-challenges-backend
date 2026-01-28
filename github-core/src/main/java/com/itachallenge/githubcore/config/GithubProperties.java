package com.itachallenge.githubcore.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "github")
public class GithubProperties {
    //SECRETS
    private String clientId;
    private String clientSecret;

    // API
    private String baseApiUrl ="https://api.github.com";
    private String userInfoUri ="/users";
    private String userProfileUri ="/user";

    //AUTH
    private String baseAuthUrl ="https://github.com";
    private String authorizationUri ="/login/oauth/authorize";
    private String tokenUri ="/login/oauth/access_token";
}
