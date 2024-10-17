package com.itachallenge.score.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionResult {

    private boolean success;
    private boolean compiled;
    private boolean execution;
    private String message;

}

