package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

/**
 * @author Luis
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SolutionUserDto {
    @JsonProperty(value = "_id", index = 0)
    private UUID uuid_SolutionUser;
    @JsonProperty(index = 1)
    private UUID uuid_challenge;
    @JsonProperty(index = 2)
    private UUID uuid_language;
    @JsonProperty(index = 3)
    private UUID uuid_user;
    @JsonProperty(index = 4)
    private String solution_Text;
}
