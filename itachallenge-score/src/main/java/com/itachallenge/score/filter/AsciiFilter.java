package com.itachallenge.score.filter;

import com.itachallenge.score.dto.ExecutionResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.BitSet;

@Component
public class AsciiFilter implements Filter {

    private static final int ASCII_SIZE = 128;
    private final BitSet allowedChars = new BitSet();
    private static final Logger log = LoggerFactory.getLogger(AsciiFilter.class);

    private Filter next;

    public AsciiFilter() {

        allowedChars.set(0, ASCII_SIZE);
        addSpecialChars();
    }

    @Override
    public ExecutionResultDto apply(String code) {

        for (int i = 0; i < code.length(); i++) {
            char currentChar = code.charAt(i);
            if (!isValidChar(currentChar)) {
                String errorMessage = String.format("Invalid character '%s' in code", currentChar);
                log.error(errorMessage);

                ExecutionResultDto executionResultDto = new ExecutionResultDto();
                executionResultDto.setCompiled(false);
                executionResultDto.setExecution(false);
                executionResultDto.setMessage(errorMessage);
                return executionResultDto;
            }
        }

        if (next != null) {
            return next.apply(code);
        }

        ExecutionResultDto executionResultDto = new ExecutionResultDto();
        executionResultDto.setPassedAllFilters(true);
        return executionResultDto;
    }

    private boolean isValidChar(char charCode) {
        return allowedChars.get(charCode);
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }

    private void addSpecialChars() {

        allowedChars.set('á');
        allowedChars.set('é');
        allowedChars.set('í');
        allowedChars.set('ó');
        allowedChars.set('ú');
        allowedChars.set('ñ');

        allowedChars.set('Á');
        allowedChars.set('É');
        allowedChars.set('Í');
        allowedChars.set('Ó');
        allowedChars.set('Ú');
        allowedChars.set('Ñ');
    }
}
