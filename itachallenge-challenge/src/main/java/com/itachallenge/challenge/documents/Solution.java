package com.itachallenge.challenge.documents;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;

@Document(collection="solutions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Solution {

    @MongoId
    @Field(name="id_solution")
    private UUID uuid;

    @Field(name="solution_text")
    private String solutionText;

    @Field(name="id_language")
    private int idLanguage;
}
