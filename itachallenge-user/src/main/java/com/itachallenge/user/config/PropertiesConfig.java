package com.itachallenge.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class PropertiesConfig {
    @Value("${url.max_length}")
    private Integer maxLength;

    public Integer getUrlMaxLength(){return maxLength;}


}
