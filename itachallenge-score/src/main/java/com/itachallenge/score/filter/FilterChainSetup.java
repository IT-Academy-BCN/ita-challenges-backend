package com.itachallenge.score.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterChainSetup {

    @Bean
    public Filter createFilterChain() {
        Filter escapeFilter = new UnescapeFilter();
        Filter asciiFilter = new AsciiFilter();
        Filter htemSanitizerFilter = new HtmlSanitizerFilter();

        htemSanitizerFilter.setNext(escapeFilter);
        escapeFilter.setNext(asciiFilter);

        return htemSanitizerFilter;
    }

}