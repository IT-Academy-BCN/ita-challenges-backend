package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.user.document.SolutionDocument;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor//(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
public class UserScoreDto {

    @JsonProperty(value = "id_challenge", index = 0)
    private UUID challengeId;

    @JsonProperty(value = "language", index = 1)
    private UUID languageID;

    @JsonProperty(value = "id_user", index = 3)
    private UUID userId;

    @JsonProperty(value = "solutions", index = 4)
    private List<SolutionDocument> solutions;

}
