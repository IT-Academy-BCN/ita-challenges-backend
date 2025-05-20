package com.itachallenge.auth.config;

import lombok.Data;

@Data
public class ClientConfig {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
