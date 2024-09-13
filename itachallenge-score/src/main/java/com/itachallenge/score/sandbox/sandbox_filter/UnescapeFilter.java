package com.itachallenge.score.sandbox.sandbox_filter;

import com.itachallenge.score.dto.ExecutionResultDto;

public class UnescapeFilter implements Filter {

    private Filter next;

    @Override
    public ExecutionResultDto apply(String input, String resultExpected) {
        String code = UnescapeJava.unescapeJavaCode(input);

        // Go to the next filter
        if (next != null) {
            return next.apply(code, resultExpected);
        }


        return new ExecutionResultDto();
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}
