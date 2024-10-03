package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class SecurityFilter implements Filter {

    private static final Logger log = Logger.getLogger(SecurityFilter.class.getName());

    @Override
    public ExecutionResult apply(String sourceCode) {
        ExecutionResult result = new ExecutionResult();


        if (sourceCode.contains("System.exit") ||
                sourceCode.contains("Runtime.getRuntime().exec") ||
                sourceCode.contains("ProcessBuilder") ||
                sourceCode.contains("java.io.File") ||
                sourceCode.contains("java.nio.file.Files")) {
            result.setSuccess(false);
            result.setMessage("Security violation: potentially harmful code detected.");
            return result;
        }


        result.setSuccess(true);
        result.setMessage("Code passed security filter");
        log.info("All filters passed. Code is safe to execute.");

        return result;
    }

    @Override
    public void setNext(Filter next) {
        // No next filter
    }
}