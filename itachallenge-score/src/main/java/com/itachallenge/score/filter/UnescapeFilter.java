package com.itachallenge.score.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnescapeFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(UnescapeFilter.class);

    private Filter next;

    @Override
    public boolean apply(String code) {
        log.info("Applying UnescapeFilter...");
        // Unescape logic here
        boolean result = next == null || next.apply(code);
        return result;
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}