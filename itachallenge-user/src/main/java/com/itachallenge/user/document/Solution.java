package com.itachallenge.user.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection="solutionTexts")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Solution {

    @Id
    @Field(name="_id")
    @JsonProperty("_id")
    private UUID uuid;

    @Field(name="solution_text")
    @JsonProperty("solution_text")
    private String solutionText;
}
