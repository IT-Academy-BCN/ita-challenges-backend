package com.itachallenge.challenge.document;

import com.itachallenge.challenge.enums.ResourceContentType;
import com.itachallenge.challenge.enums.Topic;
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
    private UUID resourceId;

    @Field(name = "title")
    private String title;

    @Field(name = "description")
    private String description;

    @Field(name = "url")
    private String url;

    @Field(name = "topic")
    private Topic topic;

    @Field(name = "content_type")
    private ResourceContentType contentType;

    @Field(name = "challenge_ids")
    private List<UUID> challengeIds;
}
