package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolutionDto {
    private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    private static final String STRING_PATTERN = "^.{1,500}$";  //max 500 characters    @Id

    @JsonProperty(value = "id_solution", index = 0)
    private UUID uuid;


    @ValidUUID(message = "Invalid UUID")
    @JsonProperty(value = "uuid_challenge", index = 1)
    private UUID idChallenge;

    @ValidUUID(message = "Invalid UUID")
    @JsonProperty(value = "uuid_language", index = 2)
    private UUID idLanguage;


    @NotEmpty(message = "cannot be empty")
    @JsonProperty(value = "solution_text", index = 3)
    private String solutionText;

    //constructor for testing with uuid, solutionText and idLanguage


    public SolutionDto(UUID uuid, UUID idLanguage, String solutionText) {
        this.uuid = uuid;
        this.idLanguage = idLanguage;
        this.solutionText = solutionText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolutionDto that = (SolutionDto) o;
        return Objects.equals(uuid, that.uuid) &&
                Objects.equals(solutionText, that.solutionText) &&
                Objects.equals(idLanguage, that.idLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, solutionText, idLanguage);
    }

    public void setChallengeId(String invalidChallengeId) {

    }

    public void setLanguageId(String validLanguageId) {
    }

}
