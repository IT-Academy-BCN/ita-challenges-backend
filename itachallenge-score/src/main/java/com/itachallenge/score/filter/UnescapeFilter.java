package com.itachallenge.score.filter;

import com.itachallenge.score.dto.ExecutionResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UnescapeFilter implements Filter {

    private Filter next;
    private static final Logger log = LoggerFactory.getLogger(UnescapeFilter.class);

@Override
public ExecutionResultDto apply(String input) {
    String code;
    try {
        code = UnescapeJava.unescapeJavaCode(input);
    } catch (Exception e) {
        String errorMessage = String.format("UnescapeFilter error: %s", e.getMessage());
        log.error(errorMessage);

        ExecutionResultDto executionResultDto = new ExecutionResultDto();
        executionResultDto.setCompiled(false);
        executionResultDto.setExecution(false);
        executionResultDto.setMessage(errorMessage);
        return executionResultDto;
    }

    // Go to the next filter
    if (next != null) {
        return next.apply(code);
    }

    ExecutionResultDto executionResultDto = new ExecutionResultDto();
    executionResultDto.setPassedAllFilters(true);
    return executionResultDto;
}

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}
