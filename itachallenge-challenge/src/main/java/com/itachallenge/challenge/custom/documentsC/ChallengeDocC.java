package com.itachallenge.challenge.custom.documentsC;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Document(collection="challenges")
//@AllArgsConstructor // recomended: all args constructor (public to be used in test)
@Getter
@ToString
public class ChallengeDocC {

    //TODO: ¿¿¿ eliminar el _class del BSON ???

    @Id
    private UUID challengeId; //TODO: investigar com es guarda la data en DB

    @Field("challenge_title")
    private String title;

    private String level;

    @Transient
    private Set<LanguageDocC> languages;

    @Field("languages")
    private Set<Integer> languagesIds;

    @Field("creation_date")
    private LocalDateTime creationDate; //TODO: investigar el tipus ok per a UTC (+ mirar mongo ISODate)

    public ChallengeDocC(UUID challengeId, String title, String level, Set<Integer> languagesIds, LocalDateTime creationDate) {
        this.challengeId = challengeId;
        this.title = title;
        this.level = level;
        this.languages = new HashSet<>();
        this.languagesIds = languagesIds;
        this.creationDate = creationDate;
    }

    public void addLanguage(LanguageDocC language) {
        languages.add(language);
    }

    public void setLanguages(Set<LanguageDocC> languages) {
        this.languages = languages;
    }
}
