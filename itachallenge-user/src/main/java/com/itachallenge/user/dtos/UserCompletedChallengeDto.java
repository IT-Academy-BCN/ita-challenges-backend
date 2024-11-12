package com.itachallenge.user.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserCompletedChallengeDto {

    private String challengeId;
    private int score;
}
