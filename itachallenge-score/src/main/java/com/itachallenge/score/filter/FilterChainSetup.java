package com.itachallenge.score.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterChainSetup {

    @Bean
    public Filter createFilterChain() {
        Filter escapeFilter = new UnescapeFilter();
        Filter htemSanitizerFilter = new HtmlSanitizerFilter();
        Filter asciiFilter = new AsciiFilter();
        Filter keywordFilter = new KeywordFilter();
        Filter securityFilter = new SecurityFilter();

        htemSanitizerFilter.setNext(escapeFilter);
        escapeFilter.setNext(asciiFilter);
        asciiFilter.setNext(keywordFilter);
        keywordFilter.setNext(securityFilter);

        return htemSanitizerFilter;
    }

}