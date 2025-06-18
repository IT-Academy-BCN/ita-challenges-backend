package com.itachallenge.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SubmitSolutionResponseDto {

    @JsonProperty("solution_text")
    private String solutionText;

    @JsonProperty("isSolved")
    private Boolean isSolved;

    @JsonProperty("timesSolved")
    private Integer timesSolved;
}
