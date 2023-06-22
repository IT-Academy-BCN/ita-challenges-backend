package com.itachallenge.challenge.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;
import java.util.UUID;

@Document(collection="languages")
@Getter
@AllArgsConstructor
public class Language {

    @Id
    @Field(name="id_languages")
    private int idLanguage;

    @Field(name="language_name")
    private String languageName;

    @Field(name="id_challenges")
    private Set<UUID> idChallenges;

}
