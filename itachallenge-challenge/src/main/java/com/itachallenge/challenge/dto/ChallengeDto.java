package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ChallengeDto {

    private UUID uuid;

    @JsonProperty("challenge_title")
    private String title;

    private Set<ReadUuidDto> related;
}
