package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UnescapeFilter implements Filter {

    private Filter next;
    private static final Logger log = LoggerFactory.getLogger(UnescapeFilter.class);

    @Override
    public ExecutionResult apply(String input) {
        String code = UnescapeJava.unescapeJavaCode(input);

        if (code == null) {
            String errorMessage = "UnescapeFilter error: Unescaped code is null";
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
        executionResult.setMessage("UnescapeFilter: Finished unescaping");
        return executionResult;
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}