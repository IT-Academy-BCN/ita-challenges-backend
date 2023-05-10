package com.itachallenge.challenge.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class PropertiesConfig {

    @Value("${url.connection_timeout}")
    private Integer connectionTimeout;//millis

    @Value("${url.maxBytesInMemory}")
    private Integer maxBytesInMemory;

    //TODO: add ita-wiki urls (also in properties) once we know where + how request resources
    // (see HttpProxy + test)

}
