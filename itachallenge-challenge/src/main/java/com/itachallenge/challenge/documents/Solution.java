package com.itachallenge.challenge.documents;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;

@Document(collection="solutions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Solution {

    @MongoId
    private UUID id_solution;

    private String solution_text;

    private int id_language;
}
