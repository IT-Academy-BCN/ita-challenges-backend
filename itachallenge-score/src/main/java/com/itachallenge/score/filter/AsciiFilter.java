package com.itachallenge.score.filter;

import java.util.BitSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsciiFilter implements Filter {

    private static final int ASCII_SIZE = 128;
    private BitSet allowedChars = new BitSet(ASCII_SIZE);
    private static final Logger log = LoggerFactory.getLogger(AsciiFilter.class);

    private Filter next;

    public AsciiFilter() {
        allowedChars.set(0, ASCII_SIZE);
    }

    @Override
    public boolean apply(String code) {
        for (int i = 0; i < code.length(); i++) {
            if (!isValidChar(code.charAt(i))) {
                log.error("The input contains a non-ASCII character: {}", code.charAt(i));
                return false;
            }
        }
        return next == null || next.apply(code);
    }

    private boolean isValidChar(char c) {
        return c < ASCII_SIZE && allowedChars.get(c);
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}
