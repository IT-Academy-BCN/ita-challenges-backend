package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserStatisticTotalDto {

    @JsonProperty(value = "uuid_user")
    private UUID userId;

    @JsonProperty(value = "uuid_language")
    private UUID languageId;

    private ChallengeStatisticTotalDto challenges;

}
