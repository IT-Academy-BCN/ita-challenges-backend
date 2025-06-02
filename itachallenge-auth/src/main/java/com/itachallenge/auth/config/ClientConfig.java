package com.itachallenge.auth.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class ClientConfig {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
    }
