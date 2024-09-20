package com.itachallenge.score.filter;

import com.itachallenge.score.dto.ExecutionResult;

public interface Filter {

    ExecutionResult apply(String input);

    void setNext(Filter next);
}
