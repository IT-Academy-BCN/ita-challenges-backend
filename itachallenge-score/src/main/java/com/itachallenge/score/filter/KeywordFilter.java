package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("keywordFilter")
@ConfigurationProperties(prefix = "keyword")
public class KeywordFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(KeywordFilter.class);

    private List<String> disallowed = new ArrayList<>();

    private Filter next;

    public void setDisallowed(List<String> disallowed) {
        this.disallowed = disallowed;
        log.info("Disallowed keywords set: " + disallowed);
    }

    @Override
    public ExecutionResult apply(String sourceCode) {
        ExecutionResult result = new ExecutionResult();

        if (disallowed == null) {
            disallowed = new ArrayList<>();
        }

        for (String keyword : disallowed) {
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