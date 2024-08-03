package com.itachallenge.score.component;

import com.itachallenge.score.dto.ExecutionResultDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExecutionResult {
    private ExecutionResultDto executionResultDto;
    private String executionResultMsg;
}
