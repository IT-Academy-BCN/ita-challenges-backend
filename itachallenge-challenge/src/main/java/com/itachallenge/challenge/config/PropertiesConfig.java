package com.itachallenge.challenge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
//@Configuration not used due there isn't any @bean method
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
