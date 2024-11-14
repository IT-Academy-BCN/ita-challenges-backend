package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserStatisticsDto extends UserSolutionDto {

    @JsonProperty(value = "challenges")
    private UserCompletedAndSavedChallengeDto completedAndSavedChallenges;

    public UserStatisticsDto(String idUser, String idLanguage, UserCompletedAndSavedChallengeDto completedAndSavedChallenges) {
        super(idUser, idLanguage);
        this.completedAndSavedChallenges = completedAndSavedChallenges;
    }
}
