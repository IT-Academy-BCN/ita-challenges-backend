package com.itachallenge.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {

    @Value("${server.tomcat.max-http-form-post-size}")
    private int maxHttpFormPostSize;

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            if (factory instanceof TomcatServletWebServerFactory) {
                ((TomcatServletWebServerFactory) factory).addConnectorCustomizers(connector -> {
                    connector.setMaxPostSize(maxHttpFormPostSize);
                    connector.setMaxSavePostSize(maxHttpFormPostSize);
                });
            }
        };
    }
}