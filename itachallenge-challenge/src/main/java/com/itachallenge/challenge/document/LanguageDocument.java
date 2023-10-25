package com.itachallenge.challenge.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.UUID;

@Document(collection="languages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LanguageDocument {

    @Id
    @Field(name="idLanguage")
    private UUID idLanguage;

    @Field(name="language_name")
    private String languageName;

}
