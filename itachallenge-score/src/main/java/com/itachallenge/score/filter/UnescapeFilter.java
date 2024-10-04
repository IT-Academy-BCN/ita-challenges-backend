package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("unescapeFilter")
public class UnescapeFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(UnescapeFilter.class);

    private Filter next;

    @Override
    public ExecutionResult apply(String input) {
        ExecutionResult result = new ExecutionResult();
        if (input == null) {
            result.setMessage("UnescapeFilter error: Unescaped code is null");
            return result;
        }

        String unescapedCode = UnescapeJava.unescapeJavaCode(input);
        result.setMessage("UnescapeFilter: Finished unescaping");

        if (next != null) {
            return next.apply(unescapedCode);
        }

        result.setMessage("UnescapeFilter: Finished unescaping");
        log.info("UnescapeFilter: Finished unescaping");
        return result;
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}