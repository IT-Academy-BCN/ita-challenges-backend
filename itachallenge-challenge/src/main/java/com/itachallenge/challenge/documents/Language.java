package com.itachallenge.challenge.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection="languages")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Language {

    @Id
    private UUID languageId;

    @Field(name="language")
    private String language;
}