package com.itachallenge.score.helper;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.BitSet;

@Getter
@Setter
public class Filter_BitSet {

    private static final int ASCII_SIZE = 128;
    private BitSet allowedChars = new BitSet(ASCII_SIZE);
    private static final Logger log = LoggerFactory.getLogger(Filter_BitSet.class);

    private String input;
    private boolean result;


    public Filter_BitSet(String input) {
        allowedChars.set(0, ASCII_SIZE);
        this.input = input;
        this.result = filter(input);

    }

    public boolean isValidChar(char c) {
        return c < ASCII_SIZE && allowedChars.get(c);
    }

    public boolean filter(String input) {

        int i = 0;
        while (i < input.length()) {
            if (!isValidChar(input.charAt(i))) {
                log.error("The input contains a {} character", input.charAt(i));
                return false;
            }
            i++;
        }
        return true;
    }
}
