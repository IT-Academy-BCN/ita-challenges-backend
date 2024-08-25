package com.itachallenge.score.filter;

import com.itachallenge.score.dto.ExecutionResultDto;

public interface Filter {

    ExecutionResultDto apply(String input, String codeExpected);

    void setNext(Filter next);
}
