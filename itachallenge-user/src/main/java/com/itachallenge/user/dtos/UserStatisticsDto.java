package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserStatisticsDto extends UserSolutionDto {

    /*
        Borrar

        Atributos heredados:

            private String userId;
            private String languageId;
     */

    @JsonProperty(value = "challenges")
    private UserCompletedAndSavedChallengeDto completedAndSavedChallenges;

    public UserStatisticsDto(String idUser, String idLanguage, UserCompletedAndSavedChallengeDto completedAndSavedChallenges) {
        super(idUser, idLanguage);
        this.completedAndSavedChallenges = completedAndSavedChallenges;
    }
}
