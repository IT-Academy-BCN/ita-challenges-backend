package com.itachallenge.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChallengesCompletedAndSavedStatisticsDTO {
    private List<CompletedChallengesDTO> completedChallenges;
    private List<SavedChallengesDTO> savedChallenges;
}
