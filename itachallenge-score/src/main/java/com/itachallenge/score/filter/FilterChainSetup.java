package com.itachallenge.score.filter;

import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
public class FilterChainSetup {

    private static final Logger log = getLogger(FilterChainSetup.class.getName());

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