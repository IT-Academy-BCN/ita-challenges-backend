package com.itachallenge.score.filter;

import com.itachallenge.score.dto.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UnescapeFilter implements Filter {

    private Filter next;
    private static final Logger log = LoggerFactory.getLogger(UnescapeFilter.class);

@Override
public ExecutionResult apply(String input) {
    String code;
    try {
        code = UnescapeJava.unescapeJavaCode(input);
    } catch (Exception e) {
        String errorMessage = String.format("UnescapeFilter error: %s", e.getMessage());
        log.error(errorMessage);

        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setCompiled(false);
        executionResult.setExecution(false);
        executionResult.setMessage(errorMessage);
        return executionResult;
    }

    // Go to the next filter
    if (next != null) {
        return next.apply(code);
    }

    ExecutionResult executionResult = new ExecutionResult();
    executionResult.setPassedAllFilters(true);
    return executionResult;
}

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}
