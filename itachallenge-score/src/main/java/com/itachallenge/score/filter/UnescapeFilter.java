package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;

public class UnescapeFilter implements Filter {

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

        return result;
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}