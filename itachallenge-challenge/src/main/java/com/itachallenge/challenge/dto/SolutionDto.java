package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.challenge.annotations.ValidGenericPattern;
import com.itachallenge.challenge.annotations.ValidUUID;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SolutionDto {
    private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    private static final String STRING_PATTERN = "^.{1,500}$";  //max 500 characters    @Id

    @JsonProperty(value = "uuid_solution", index = 0)
    private UUID uuid;

    //@ValidGenericPattern(pattern = STRING_PATTERN, message = "Solution text cannot be empty")
    @NotEmpty(message = "cannot be empty")
    @JsonProperty(value = "solution_text", index = 1)
    private String solutionText;

    @ValidUUID(message = "Invalid UUID")
    @JsonProperty(value = "uuid_language", index = 2)
    private UUID idLanguage;

    @ValidUUID(message = "Invalid UUID")
    @JsonProperty(value = "uuid_challenge", index = 3)
    private UUID idChallenge;

    //constructor for testing with uuid, solutionText and idLanguage
    public SolutionDto(UUID uuid, String solutionText, UUID idLanguage) {
        this.uuid = uuid;
        this.solutionText = solutionText;
        this.idLanguage = idLanguage;
    }

}
