package com.itachallenge.userinteraction.document;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.ToString;
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
public abstract class InteractionDocument {

    @Id
    @Field("_id")
    protected UUID uuid;

    @Field("userId")
    @Indexed
    protected UUID userId;

    @Field("challengeId")
    @Indexed
    protected UUID challengeId;

    @CreatedDate
    @Field(name = "createdAt")
    protected LocalDateTime createdAt;
}
