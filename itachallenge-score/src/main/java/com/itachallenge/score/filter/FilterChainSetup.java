package com.itachallenge.score.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FilterChainSetup {

    private FilterChainSetup() {
    }

    @Bean
    public static Filter createFilterChain() {
        Filter asciiFilter = new AsciiFilter();
        Filter escapeFilter = new UnescapeFilter();
        escapeFilter.setNext(asciiFilter);
        return escapeFilter;
    }
}
