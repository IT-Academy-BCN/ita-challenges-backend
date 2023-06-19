package com.itachallenge.challenge.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection="solutions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Solution {

    @Id
    @Field(name="id_solution")
    private UUID uuid;

    @Field(name="solution_text")
    private String solutionText;

    @Field(name="id_language")
    private int idLanguage;
}
