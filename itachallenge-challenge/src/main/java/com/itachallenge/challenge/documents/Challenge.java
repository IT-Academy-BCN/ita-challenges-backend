package com.itachallenge.challenge.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Document(collection="challenges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Challenge {

    //1. añadir fields a todas las variables con guion bajo; variable con camelCase
    @MongoId
    @Field(name="id_challenge")
    private UUID uuid;

    private String level;   //valor seteado fom properties

    private String title;

    @DBRef
    @Field(name="languages")
    private Set<Language> languages;

    @Field(name="creation_date")
    private LocalDate creationDate;

    //Next fields not priority for the moment

    private Detail detail;

    @DBRef
    private List<Solution> solutions;

    @Field(name="related_challenges")
    private Set<UUID> relatedChallenges;

    private Set<UUID> resources;

}
