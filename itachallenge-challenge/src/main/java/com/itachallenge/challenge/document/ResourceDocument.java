package com.itachallenge.challenge.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;
import java.util.UUID;

@Document(collection = "resources")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDocument {

    @Id
    @Field("_id")
    private UUID uuid;

    @Field(name = "title")
    private String title;

    @Field(name = "description")
    private String description;

    @Field(name = "url")
    private String url;

    @Field(name = "topic")
    private String topic;

    @Field(name = "content_type")
    private String contentType;

    @Field(name = "challenge_ids")
    private List<UUID> challengeIds;
}
