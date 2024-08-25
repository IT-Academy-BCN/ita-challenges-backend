package com.itachallenge.score.filter;

import com.itachallenge.score.dto.ExecutionResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.BitSet;

public class AsciiFilter implements Filter {

    private static final int ASCII_SIZE = 128;
    private final BitSet allowedChars = new BitSet(ASCII_SIZE);
    private static final Logger log = LoggerFactory.getLogger(AsciiFilter.class);

    private Filter next;

    public AsciiFilter() {
        allowedChars.set(0, ASCII_SIZE);
    }

    @Override
    public ExecutionResultDto apply(String code, String resultExpected) {

        for (int i = 0; i < code.length(); i++) {
            char currentChar = code.charAt(i);
            if (!isValidChar(currentChar)) {
                String errorMessage = String.format("The input contains a non-ASCII character: '%c' at position %d", currentChar, i);
                log.error(errorMessage);

                ExecutionResultDto executionResultDto = new ExecutionResultDto();
                executionResultDto.setCompile(false);
                executionResultDto.setExecution(false);
                executionResultDto.setMessage(errorMessage);
                return executionResultDto;
            }
        }

        // Go to the next filter
        if (next != null) {
            return next.apply(code, resultExpected);
        }

        return new ExecutionResultDto();
    }

    private boolean isValidChar(char charCode) {
        return allowedChars.get(charCode);
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}
