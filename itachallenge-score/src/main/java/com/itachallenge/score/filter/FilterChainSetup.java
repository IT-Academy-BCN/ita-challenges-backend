package com.itachallenge.score.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FilterChainSetup {

    private FilterChainSetup() {
    }

    @Bean
    public static Filter createFilterChain() {

        JavaContainerFilter javaContainerFilter = new JavaContainerFilter();

        Filter escapeFilter = new UnescapeFilter();
        Filter asciiFilter = new AsciiFilter();
        Filter compilationFilter = new CompilationFilter();
        Filter executionFilter = new ExecutionFilter();

        escapeFilter.setNext(asciiFilter);
        asciiFilter.setNext(javaContainerFilter);
        javaContainerFilter.setNext(compilationFilter);
        compilationFilter.setNext(executionFilter);

        return escapeFilter;
    }
}
