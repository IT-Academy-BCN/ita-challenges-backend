package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KeywordFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(KeywordFilter.class);

    private static final String[] DISALLOWED_KEYWORDS = {
            "import", "class", "interface", "enum", "package"
    };

    private Filter next;

    @Override
    public ExecutionResult apply(String sourceCode) {
        ExecutionResult result = new ExecutionResult();

        for (String keyword : DISALLOWED_KEYWORDS) {
            if (sourceCode.contains(keyword)) {
                result.setSuccess(false);
                result.setMessage("Security violation: disallowed keyword detected: " + keyword);
                return result;
            }
        }


        result.setMessage("Code passed keyword filter");
        log.info("Code passed keyword filter");


        if (next != null) {
            return next.apply(sourceCode);
        }

        return result;
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}