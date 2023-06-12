package com.itachallenge.challenge.documents;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Document(collection="challenges")
@Data
@AllArgsConstructor
public class Challenge {

    @MongoId
    @Field(name="id_challenge")
    private String uuid;

    @Field(name="level")
    private String level;   //valor seteado fom properties

    @Field(name="challenge_title")
    private String title;

    @Field(name="languages")
    private Set<String> languages;

    @Field(name="creation_date")
    private LocalDate creationDate;

    //Next fields not priority for the moment

    @Field(name="detail")
    private Detail detail;

    @Field(name="solutions")
    private List<String> solutions;

    @Field(name="related")
    private Set<String> related;

    @Field(name="resources")
    private Set<String> resources;

    @Field(name = "tags")
    private Set<String> tags;

}
