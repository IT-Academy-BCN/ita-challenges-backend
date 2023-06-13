package com.itachallenge.challenge.custom.documentsC;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Document(collection="challenges")
@AllArgsConstructor // recomended: all args constructor (public to be used in test)
@Getter
//@ToString
public class ChallengeDocWithDBRefC {

    //TODO: ¿¿¿ eliminar el _class del BSON ???

    @Id
    private UUID challengeId; //TODO: investigar com es guarda la data en DB

    @Field("challenge_title")
    private String title;

    private String level;

    @DBRef //TODO: preguntar al jonatan com ho vol: si @DBRef amb blocking code o només ids + reconsultar a la DB
    private Set<LanguageDocC> languages;

    @Field("creation_date")
    private LocalDateTime creationDate; //TODO: investigar el tipus ok per a UTC (+ mirar mongo ISODate)
}
