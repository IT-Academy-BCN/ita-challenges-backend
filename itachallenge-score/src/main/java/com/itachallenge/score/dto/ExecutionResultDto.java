package com.itachallenge.score.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExecutionResultDto {

    private boolean compiled;
    private boolean execution;
    private boolean resultCodeMatch;
    private String message;

    @JsonCreator
    public ExecutionResultDto(@JsonProperty("compiled") boolean compiled,
                              @JsonProperty("message") String message) {
        this.compiled = compiled;
        this.message = message;
    }



}
