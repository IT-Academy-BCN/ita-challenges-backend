package com.itachallenge.user.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;


@Getter
@AllArgsConstructor
public class SolutionDocument {

    @Id
    @Field(name="id_solution")
    private UUID uuid;

    @Field(name="solution_text")
    private String solutionText;

}
