package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor // Necessary for Builder to work

@Builder

public class UserLanguageChallengesDto {

    @JsonProperty("uuid_user")
    private String uuidUser;

    @JsonProperty("uuid_language")
    private String uuidLanguage;

    private ChallengesListsDto challenges;

}
