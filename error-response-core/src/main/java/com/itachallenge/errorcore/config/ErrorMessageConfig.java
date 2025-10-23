package com.itachallenge.errorcore.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class ErrorMessageConfig {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource src = new ReloadableResourceBundleMessageSource();
        src.setBasenames(
                "classpath:core-messages", // shared bundle in core
                "classpath:messages"            // service-local bundle
        );
        src.setDefaultEncoding("UTF-8");
        src.setFallbackToSystemLocale(false);
        return src;
    }
}
