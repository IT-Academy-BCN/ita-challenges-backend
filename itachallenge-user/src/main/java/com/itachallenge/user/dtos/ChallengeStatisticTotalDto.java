package com.itachallenge.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChallengeStatisticTotalDto {

    private int count;
    private int completed;
    private int saved;
    private int score_pending;
    private int passed;
}
