package com.itachallenge.score.filter;

public class FilterChainSetup {

    public static Filter createFilterChain() {
        Filter asciiFilter = new AsciiFilter();
        Filter escapeFilter = new UnescapeFilter();
        escapeFilter.setNext(asciiFilter);
        return escapeFilter;
    }
}
