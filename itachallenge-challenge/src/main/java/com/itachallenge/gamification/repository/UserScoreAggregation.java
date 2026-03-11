package com.itachallenge.gamification.repository;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserScoreAggregation {

    @Field("user_id")
    private UUID userId;

    private String username;
    private int totalPoints;
}
