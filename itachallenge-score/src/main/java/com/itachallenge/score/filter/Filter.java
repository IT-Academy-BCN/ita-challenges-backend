package com.itachallenge.score.filter;

import com.itachallenge.score.dto.ExecutionResultDto;

public interface Filter {

    ExecutionResultDto apply(String input);

    void setNext(Filter next);
}
