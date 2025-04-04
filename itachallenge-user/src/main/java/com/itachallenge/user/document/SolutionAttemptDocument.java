package com.itachallenge.user.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SolutionAttemptDocument {

    @Id
    @Field(name="id_solution")
    private UUID uuid;

    @Field(name="solution_text")
    private String solutionText;

}
