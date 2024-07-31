package com.itachallenge.score.filter;

public class FilterChainSetUp {

    public static Filter createFilterChain() {
        Filter asciiFilter = new AsciiFilter();
        Filter escapeFilter = new EscapeFilter();
        escapeFilter.setNext(asciiFilter);
        return escapeFilter;
    }
}
