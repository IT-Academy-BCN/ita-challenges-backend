package com.itachallenge.score.filter;

import com.itachallenge.score.dto.ExecutionResult;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HtmlSanitizerFilter implements Filter{

    private static final Logger log = LoggerFactory.getLogger(HtmlSanitizerFilter.class);
    private Filter next;

@Override
public ExecutionResult apply(String code) {
    PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
    String sanitizedCode;
    try {
        sanitizedCode = policy.sanitize(code);
    } catch (Exception e) {
        String errorMessage = String.format("HtmlSanitizerFilter error: %s", e.getMessage());
        log.error(errorMessage);

        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setCompiled(false);
        executionResult.setExecution(false);
        executionResult.setMessage(errorMessage);
        return executionResult;
    }
    log.info("Sanitized code: {}", sanitizedCode);

    // Go to the next filter
    if (next != null) {
        return next.apply(sanitizedCode);
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
