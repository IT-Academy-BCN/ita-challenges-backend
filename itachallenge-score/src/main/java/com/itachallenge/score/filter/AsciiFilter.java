package com.itachallenge.score.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsciiFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AsciiFilter.class);

    private Filter next;

    @Override
    public boolean apply(String code) {
        log.info("Applying AsciiFilter...");
        // ASCII validation logic here
        boolean result = next == null || next.apply(code);
        return result;
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}