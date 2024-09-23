package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;

public class UnescapeFilter implements Filter {

    private Filter next;

    @Override
    public ExecutionResult apply(String input) {
        ExecutionResult result = new ExecutionResult();
        if (input == null) {
            result.setCompiled(false);
            result.setExecution(false);
            result.setMessage("UnescapeFilter error: Unescaped code is null");
            return result;
        }

        String unescapedCode = UnescapeJava.unescapeJavaCode(input);
        result.setCompiled(true);
        result.setExecution(true);
        result.setMessage("UnescapeFilter: Finished unescaping");

        if (next != null) {
            return next.apply(unescapedCode);
        }

        return result;
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}