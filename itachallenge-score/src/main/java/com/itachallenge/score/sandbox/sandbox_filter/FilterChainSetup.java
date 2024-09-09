package com.itachallenge.score.sandbox.sandbox_filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterChainSetup {

    @Bean
    public Filter createFilterChain(JavaContainerFilter javaContainerFilter, CompileExecuterFilter compileExecuterFilter) {
        Filter escapeFilter = new UnescapeFilter();
        Filter asciiFilter = new AsciiFilter();

        escapeFilter.setNext(asciiFilter);
        asciiFilter.setNext(javaContainerFilter);
        javaContainerFilter.setNext(compileExecuterFilter);

        return escapeFilter;
    }
}