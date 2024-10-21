package com.itachallenge.challenge.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.challenge.annotations.ValidUUID;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection="solutions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SolutionDocument {

    @Id
    @Field(name="id_solution")
    private UUID uuid;

    @Field(name="solution_text")
    private String solutionText;

    @Field(name="language")
    private UUID idLanguage;

    private UUID idChallenge;

    //constructor para el test
    public SolutionDocument(UUID uuid, String solutionText, UUID idLanguage) {
        this.uuid = uuid;
        this.solutionText = solutionText;
        this.idLanguage = idLanguage;
    }







    }
