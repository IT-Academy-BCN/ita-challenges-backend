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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@AllArgsConstructor
public abstract class InteractionDocument {

    @Id
    @Field("_id")
    @EqualsAndHashCode.Include
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
