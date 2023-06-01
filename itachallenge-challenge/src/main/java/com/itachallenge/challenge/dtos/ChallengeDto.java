package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChallengeDto {

    @JsonProperty("id_challenge")
    private UUID uuid;

}
