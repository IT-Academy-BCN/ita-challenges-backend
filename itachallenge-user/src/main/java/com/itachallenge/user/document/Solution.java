package com.itachallenge.user.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection="solutionTexts")
@Getter
@AllArgsConstructor
public class Solution {

    @Id
    @Field(name="_id")
    private UUID uuid;

    @Field(name="solution_text")
    private String solutionText;
}
