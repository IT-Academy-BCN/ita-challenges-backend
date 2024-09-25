package com.itachallenge.score.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExecutionResultDto {

    private boolean compile;
    private boolean execution;
    private boolean resultCodeMatch;
    private String message;

}
