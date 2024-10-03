package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HtmlSanitizerFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(HtmlSanitizerFilter.class);
    private Filter next;

    @Override
    public ExecutionResult apply(String code) {
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        ExecutionResult executionResult = new ExecutionResult();

        if (code == null) {
            executionResult.setMessage("Code is null");
            if (next != null) {
                return next.apply(code);
            }
            return executionResult;
        }

        String sanitizedCode = policy.sanitize(code);
        log.info("Code passed HTML sanitizer");

        // Go to the next filter
        if (next != null) {
            return next.apply(sanitizedCode);
        }


        executionResult.setMessage("Code passed HTML sanitizer");
        return executionResult;
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }

    public String sanitize(String code) {
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        return policy.sanitize(code);
    }
}