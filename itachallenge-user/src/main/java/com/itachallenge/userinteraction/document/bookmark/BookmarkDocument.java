package com.itachallenge.userinteraction.document.bookmark;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Document(collection="bookmarks")
public class BookmarkDocument {

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
