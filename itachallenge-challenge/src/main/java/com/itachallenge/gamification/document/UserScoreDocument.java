package com.itachallenge.gamification.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "user_score_history")
public class UserScoreDocument {

    @Id
    @Field("_id")
    private UUID id;

    @Field("user_id")
    private UUID userId;

    @Field("challenge_id")
    private UUID challengeId;

    @Field("points")
    private int points;

    @Field("created_at")
    private LocalDateTime createdAt;
}
