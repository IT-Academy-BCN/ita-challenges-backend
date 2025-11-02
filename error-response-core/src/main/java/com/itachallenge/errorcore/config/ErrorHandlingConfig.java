package com.itachallenge.errorcore.config;

import com.itachallenge.errorcore.builder.ErrorResponseBuilder;
import com.itachallenge.errorcore.exceptionhandler.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Manual configuration exposing shared error-handling components.
 */
@Configuration
public class ErrorHandlingConfig {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource src = new ReloadableResourceBundleMessageSource();
        src.setBasenames("classpath:core-messages", "classpath:messages");
        src.setDefaultEncoding("UTF-8");
        src.setFallbackToSystemLocale(false);
        return src;
    }

    @Bean
    public ErrorResponseBuilder errorResponseBuilder(MessageSource messageSource) {
        return new ErrorResponseBuilder(messageSource);
    }

    @Bean
    @ConditionalOnMissingBean(GlobalExceptionHandler.class)
    public GlobalExceptionHandler globalExceptionHandler(ErrorResponseBuilder builder) {
        // Anonymous subclass to register handler logic, if needed
        return new GlobalExceptionHandler(builder);
    }
}
