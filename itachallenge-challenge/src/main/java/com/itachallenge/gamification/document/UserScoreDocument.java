package com.itachallenge.gamification.document;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
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
@CompoundIndex(def = "{'user_id': 1, 'created_at': 1}", name = "user_scores_idx")
public class UserScoreDocument {

    @Id
    @Field("_id")
    private UUID id;

    @Field("user_id")
    private UUID userId;

    @Field("username")
    private String username;

    @Field("challenge_id")
    private UUID challengeId;

    @Field("points")
    private Integer points;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
}
