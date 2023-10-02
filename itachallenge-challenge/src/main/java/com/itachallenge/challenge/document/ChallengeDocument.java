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
    private UUID challengeId;

    @Field(name="challenge_title")
    private String challengeTitle;

    @Field(name="level")
    private String challengeLevel;   //valor seteado fom properties

    @Field(name="creation_date")
    private LocalDateTime challengeCreationDate;

    @Field(name="detail")
    private DetailDocument challengeDetail;

    @Field(name="languages")
    private Set<LanguageDocument> challengeLanguages;

    @Field(name="solutions")
    private List<UUID> challengeSolutions;

    @Field(name="resources")
    private Set<UUID> challengeResources;

    @Field(name="related")
    private Set<UUID> challengeRelatedChallenges;

}
