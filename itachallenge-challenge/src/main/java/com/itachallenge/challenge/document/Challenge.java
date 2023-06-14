package com.itachallenge.challenge.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Set;
import java.util.UUID;

@Document(collection="challenges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Challenge {

    @Id
    private String id;

    @Field(name="id_challenge")
    private String uuid;

    @Field(name="level")
    private String level;   //valor seteado fom properties

    @Field(name="challenge_title")
    private String title;

    @Field(name="languages")
    private Set<String> languages;

    @Field(name="related")
    private Set<String> relatedChallenges;
}
