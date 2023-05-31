package com.itachallenge.challenge.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Document(collection="challenges")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Challenge {

    @Id
    private UUID challengeId;

    @Field(name="name")
    private String title;

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
    private List<String> related;

}
