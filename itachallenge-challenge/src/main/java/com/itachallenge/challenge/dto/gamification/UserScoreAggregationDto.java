package com.itachallenge.challenge.dto.gamification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserScoreAggregationDto {

    @Field("user_id")
    @JsonProperty("user_id")
    private UUID userId;

    private String username;
    private int totalPoints;
}
