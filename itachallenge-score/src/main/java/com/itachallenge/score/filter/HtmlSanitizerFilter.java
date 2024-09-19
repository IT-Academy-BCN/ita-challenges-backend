package com.itachallenge.score.filter;

import com.itachallenge.score.dto.ExecutionResultDto;
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
    public ExecutionResultDto apply(String code) {
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        String sanitizedCode = policy.sanitize(code);
        log.info("Sanitized code: " + sanitizedCode);

        // Go to the next filter
        if (next != null) {
            return next.apply(sanitizedCode);
        }

        return new ExecutionResultDto();

    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}
