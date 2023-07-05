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
@NoArgsConstructor
@Getter
@NoArgsConstructor
public class ChallengeDocument {

    @Id
    @Field(name="_id")
    private UUID idChallenge;

    @Field(name="level")
    private String level;   //valor seteado fom properties

    @Field(name="challenge_title")
    private String title;

    @Field(name="languages")
    private Set<LanguageDocument> languages;

    @Field(name="creation_date")
    private LocalDateTime creationDate;

    //Next fields not priority for the moment

    @Field(name="detail")
    private DetailDocument detail;


    @Field(name="solutions")
    private List<SolutionDocument> solutions;

    @Field(name="related_challenges")
    private Set<UUID> relatedChallenges;

    @Field(name="resources")
    private Set<UUID> resources;

    public ChallengeDocument(UUID idChallenge, String level, String title, Set<LanguageDocument> languages,
                             LocalDateTime creationDate, DetailDocument detail, List<SolutionDocument> solutions,
                             Set<UUID> relatedChallenges, Set<UUID> resources) {
        this.idChallenge = idChallenge;
        this.level = level;
        this.title = title;
        this.languages = languages;
        this.creationDate = LocalDateTime.now();
        this.detail = detail;
        this.solutions = solutions;
        this.relatedChallenges = relatedChallenges;
        this.resources = resources;
    }
}
