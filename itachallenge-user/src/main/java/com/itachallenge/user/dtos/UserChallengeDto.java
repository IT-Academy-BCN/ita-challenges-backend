package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // Required to use builder
@Builder // This helps in the creation of the instance
public class UserChallengeDto {

    @JsonProperty("uuid_challenge")
    private String uuidChallenge;

    private Integer score; // It is Integer and not int in order to allow a null value, this allows polymorphism

}
