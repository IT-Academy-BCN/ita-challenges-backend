package com.itachallenge.score.filter;

import com.itachallenge.score.dto.ExecutionResult;
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
public ExecutionResult apply(String code) {
    ExecutionResult executionResult = new ExecutionResult();
    if (code == null || code.isEmpty()) {
        executionResult.setCompiled(false);
        executionResult.setMessage("Code is empty");
        return executionResult;
    }

    for (int i = 0; i < code.length(); i++) {
        char c = code.charAt(i);
        if (!isValidChar(c)) {
            log.error("ASCII FILTER ERROR: Invalid character '{}' at index {} in code", c, i);
            executionResult.setCompiled(false);
            executionResult.setMessage("ASCII FILTER ERROR: Invalid character '" + c + "' at index " + i + " in code");
            return executionResult;
        }
    }

    if (next != null) {
        return next.apply(code);
    }

    executionResult.setCompiled(true);
    executionResult.setExecution(true);
    executionResult.setMessage("Code passed ASCII filter");
    return executionResult;
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