package com.itachallenge.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChallengesCompletedAndSavedStatisticsDTO {
    private CompletedChallengesDTO completedChallenges;
    private SavedChallengesDTO savedChallenges;
}
