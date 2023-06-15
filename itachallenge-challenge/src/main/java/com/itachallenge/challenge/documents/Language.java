package com.itachallenge.challenge.documents;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Set;
import java.util.UUID;

@Document(collection="languages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Language {

    @MongoId
    @Field(name="id_languages")
    private int idLanguage;

    @Field(name="language_name")
    private String languageName;

    @Field(name="id_challenges")
    private Set<UUID> idChallenges;

}
