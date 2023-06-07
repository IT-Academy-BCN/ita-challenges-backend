package com.itachallenge.challenge.documents;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Document(collection="challenges")
@Getter
public class Challenge {

    @MongoId
    @Field(name="id_challenge")
    private UUID uuid;

    @Field(name="level")
    private String level;   //valor seteado fom properties

    @Field(name="challenge_title")
    private String title;

    @DBRef
    @Field(name="languages")
    private Set<Language> languages;

    @Field(name="creation_date")
    private LocalDateTime creationDate;

    //Next fields not priority for the moment

    @Field(name="detail")
    private Detail detail;

    @DBRef
    @Field(name="solutions")
    private List<Solution> solutions;

    @Field(name="related_challenges")
    private Set<UUID> relatedChallenges;

    @Field(name="resources")
    private Set<UUID> resources;

    public Challenge(UUID uuid, String level, String title, Set<Language> languages,
                     LocalDateTime creationDate, Detail detail, List<Solution> solutions,
                     Set<UUID> relatedChallenges, Set<UUID> resources) {
        this.uuid = uuid;
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
