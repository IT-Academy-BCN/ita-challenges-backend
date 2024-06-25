package com.itachallenge.user.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SolutionDocument {

    @Id
    @Field(name="id_solution")
    private UUID uuidSolution;

    @Field(name="solution_text")
    private String solutionText;

}
