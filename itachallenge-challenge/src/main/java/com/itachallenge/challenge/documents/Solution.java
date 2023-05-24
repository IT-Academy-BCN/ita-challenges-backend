package com.itachallenge.challenge.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection="solutions")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Solution {

    @Id
    private UUID solutionId;

    @Field(name="solution")
    private String solution;

    @Field(name="challengeId")
    private UUID challengeId;

    @Field(name="languageId")
    private UUID languageId;
}