package com.itachallenge.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "server.tomcat")
public class TomcatConfig {

    private int maxHttpFormPostSize;
}
