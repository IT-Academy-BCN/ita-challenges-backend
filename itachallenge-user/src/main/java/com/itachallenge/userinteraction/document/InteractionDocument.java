package com.itachallenge.userinteraction.document;

import lombok.*;
import lombok.experimental.SuperBuilder;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class InteractionDocument {

    @Id
    @Field("_id")
    private UUID uuid;

    @Field("userId")
    @Indexed
    private UUID userId;

    @Field("challengeId")
    @Indexed
    private UUID challengeId;

    @CreatedDate
    @Field(name = "createdAt")
    private LocalDateTime createdAt;
}
