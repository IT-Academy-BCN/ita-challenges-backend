package com.itachallenge.challenge.documents;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Set;
import java.util.UUID;

@Document(collection="languages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Language {

    @MongoId
    private int id_language;

    private String language_name;

    private Set<UUID> id_challenges;

}
