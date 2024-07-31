package com.itachallenge.score.helper;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Setter
public class SimpleValidate {

    private static final Logger log = LoggerFactory.getLogger(SimpleValidate.class);

    private String input;
    private boolean result;

    public SimpleValidate(String input) {
        this.input = input;
        this.result = filter();
    }

    private boolean filter() {

        for (int i = 0; i < input.length(); i++) {
            int ascii = input.charAt(i);
            if (ascii > 127) {
                log.error("The input contains a {} character", input.charAt(i));
                return false;

            }
        }
        log.info("The input contains only ASCII characters");
        return true;
    }
}
