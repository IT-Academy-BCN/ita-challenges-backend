package com.itachallenge.challenge.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Document(collection="challenges")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Challenge {

    @Id
    @Field("_id")
    private UUID uuid;

    @Field(name="level")
    private String level;   //valor seteado fom properties

    @Field(name="challenge_title")
    private String title;


    @Field(name="languages")
    private Set<UUID> languages;

    @Field(name="creation_date")
    private LocalDate creationDate;

    //Next fields not priority for the moment

    @Field(name="detail")
    private Detail detail;


    @Field(name="solutions")
    private List<UUID> solutions;

    @Field(name="related_challenges")
    private Set<UUID> relatedChallenges;

    @Field(name="resources")
    private Set<UUID> resources;
}
