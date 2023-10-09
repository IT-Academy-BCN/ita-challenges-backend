package com.itachallenge.challenge.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection="solutions")
@Data
@AllArgsConstructor
public class SolutionDocument {

    @Id
    @Field(name="id_solution")
    private UUID solutionId;

    @Field(name="solution_text")
    private String solutionText;

    @Field(name="language")
    private UUID idLanguage;
}
