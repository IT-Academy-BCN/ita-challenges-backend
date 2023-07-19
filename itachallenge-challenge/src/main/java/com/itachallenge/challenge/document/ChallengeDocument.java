package com.itachallenge.challenge.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Document(collection="challenges")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDocument {

    @Id
    @Field("_id")
    private UUID uuid;

    @Field(name="challenge_title")
    private String title;

    @Field(name="level")
    private String level;   //valor seteado fom properties

    @Field(name="creation_date")
    private LocalDateTime creationDate;

    @Field(name="detail")
    private DetailDocument detail;

    @Field(name="languages")
    private Set<LanguageDocument> languages;

    @Field(name="solutions")
    private List<UUID> solutions;

    @Field(name="resources")
    private Set<UUID> resources;

    @Field(name="related")
    private Set<UUID> relatedChallenges;

}
