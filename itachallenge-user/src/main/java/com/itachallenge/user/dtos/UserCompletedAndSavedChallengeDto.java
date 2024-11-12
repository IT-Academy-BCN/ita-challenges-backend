package com.itachallenge.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCompletedAndSavedChallengeDto {

    private List<UserCompletedChallengeDto> completedChallenge;
    private List<String> challengesSaved;

}
