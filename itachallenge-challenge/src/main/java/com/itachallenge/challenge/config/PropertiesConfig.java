package com.itachallenge.challenge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component //There's none bean declaration -> it's not a @Configuration
//Setters NOT needed. Values injected anyway (tested + ok)
public class PropertiesConfig {

    @Value("${url.connection_timeout}")
    private Integer connectionTimeout;//millis

    @Value("${url.maxBytesInMemory}")
    private Integer maxBytesInMemory;

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public Integer getMaxBytesInMemory() {
        return maxBytesInMemory;
    }
}
