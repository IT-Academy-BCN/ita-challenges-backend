package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserStatisticTotalDto {

    @JsonProperty(value = "uuid_user")
    private UUID userId;

    @JsonProperty(value = "uuid_language")
    private UUID languageId;

    private ChallengeStatisticTotalDto challenges;

}
