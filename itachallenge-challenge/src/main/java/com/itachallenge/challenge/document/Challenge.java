package com.itachallenge.challenge.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.UUID;

@Document(collection="challenges")
@AllArgsConstructor
//@NoArgsConstructor
@Data
public class Challenge {

    @Id
    private UUID challengeId;

    @Field(name="name")
    private String name;

    @Field(name="level")
    private Level level;

    @Field(name="creationDate")
    private Date creationDate;

    @Field(name="details")
    private Detail details;

    @Field(name="solutions")
    private Solution[] solutions;

    @Field(name="languages")
    private Language[] languages;

    @Field(name="resources")
    private Resource[] resources;

    @Field(name="related")
    private Challenge[] related;

    //pureba para crear challenge en bd
    public Challenge() {
        this.challengeId = UUID.randomUUID();
    }

    public Challenge(UUID challengeId) {
        this.challengeId = challengeId;
    }
}
