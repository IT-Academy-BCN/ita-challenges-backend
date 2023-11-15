package com.itachallenge.user.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserSolutionScore {

    @Field(value = "id_challenge")
    private UUID challengeId;

    @Field(value = "language")
    private UUID languageId;

    @Field(value = "id_user")
    private UUID userId;

    private String solutionText;


}