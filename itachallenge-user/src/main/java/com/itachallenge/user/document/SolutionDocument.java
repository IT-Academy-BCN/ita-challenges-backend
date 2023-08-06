package com.itachallenge.user.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection="solutions")
@Getter
@AllArgsConstructor

public class SolutionDocument {

    @Id
    @Field(name="id_solution")
    private UUID uuid;

    @Field(name="solution_text")
    private String solutionText;

    @Field(name="id_language")
    private UUID idLanguage;
}
