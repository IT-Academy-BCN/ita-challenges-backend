package com.itachallenge.challenge.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddFavoriteRequest {

    @Schema(description = "Challenge identifier to be added as favorite", example = "12345")
    private String challengeId;

}
