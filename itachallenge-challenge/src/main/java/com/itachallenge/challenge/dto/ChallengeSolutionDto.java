package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChallengeSolutionDto {

    @JsonProperty("uuid_challenge")
    private UUID uuidChallenge;

    @JsonProperty("uuid_language")
    private UUID uuidLanguage;

    @JsonProperty("solutions")
    private List<SolutionDto> solutions;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SolutionDto {

        @JsonProperty("uuid_solution")
        private UUID uuidSolution;

        @NotEmpty(message = "Solution text cannot be empty")
        @JsonProperty("solution_text")
        private String solutionText;


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SolutionDto that = (SolutionDto) o;
            return Objects.equals(uuidSolution, that.uuidSolution) &&
                    Objects.equals(solutionText, that.solutionText);
        }

        @Override
        public int hashCode() {
            return Objects.hash(uuidSolution, solutionText);
        }

    }
}
