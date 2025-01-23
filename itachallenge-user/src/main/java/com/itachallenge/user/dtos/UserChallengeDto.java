package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itachallenge.user.helper.ChallengeJsonSerializer;
import lombok.*;

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor // Required to use builder

@Builder // This helps in the creation of the instance

@JsonSerialize(using = ChallengeJsonSerializer.class)
public class UserChallengeDto {

    @JsonProperty("uuid_challenge")
    private String uuidChallenge;


}
