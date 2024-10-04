package com.itachallenge.score.filter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
public class FilterChainSetup {

    private static final Logger log = Logger.getLogger(FilterChainSetup.class.getName());

    private final Filter unescapeFilter;
    private final Filter htmlSanitizerFilter;
    private final Filter asciiFilter;
    private final Filter keywordFilter;
    private final Filter securityFilter;

    public FilterChainSetup(
            @Qualifier("unescapeFilter") Filter unescapeFilter,
            @Qualifier("htmlSanitizerFilter") Filter htmlSanitizerFilter,
            @Qualifier("asciiFilter") Filter asciiFilter,
            @Qualifier("keywordFilter") Filter keywordFilter,
            @Qualifier("securityFilter") Filter securityFilter) {
        this.unescapeFilter = unescapeFilter;
        this.htmlSanitizerFilter = htmlSanitizerFilter;
        this.asciiFilter = asciiFilter;
        this.keywordFilter = keywordFilter;
        this.securityFilter = securityFilter;
    }

    @Bean
    public Filter createFilterChain() {
        log.info("Creating filter chain");

        log.info("Setting up filter chain");
        htmlSanitizerFilter.setNext(unescapeFilter);
        unescapeFilter.setNext(asciiFilter);
        asciiFilter.setNext(keywordFilter);
        keywordFilter.setNext(securityFilter);

        log.info("Filter chain created successfully");
        return htmlSanitizerFilter;
    }
}