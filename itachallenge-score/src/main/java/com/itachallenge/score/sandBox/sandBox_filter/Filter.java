package com.itachallenge.score.sandBox.sandBox_filter;

import com.itachallenge.score.dto.ExecutionResultDto;

public interface Filter {

    ExecutionResultDto apply(String input, String expectedCode);

    void setNext(Filter next);
}
