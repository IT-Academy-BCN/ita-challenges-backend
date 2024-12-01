package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ChallengeStatisticTotalDto {
    private int completed;

    private int saved;

    @JsonProperty(value = "score_pending")
    private int scorePending;

    private int passed;
}
