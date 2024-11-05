package com.itachallenge.user.dtos;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ChallengeStatisticTotalDto {

    private int count;
    private int completed;
    private int saved;
    private int scorePending;
    private int passed;
}
